package cn.xmlly.common.utils.redis;

import cn.xmlly.common.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author lixingjia
 * @since 2018/12/12
 **/
@Slf4j
@ConditionalOnMissingClass
public class RedisDistributionLock {

    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * Lock key path.
     */
    private String lockKey;

    private final String LOCK_PREFIX = "redissonlock_";

    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private int expireMsecs = 60 * 1000;

    /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMsecs = 10 * 1000;
    /**
     * 用于识别锁的编号，防止被别的锁释放
     */
    private String uuid;


    public RedisDistributionLock(String lockKey) {
        this.lockKey = LOCK_PREFIX + lockKey;
    }

    public RedisDistributionLock(String lockKey, int timeoutMsecs) {
        this(lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    public RedisDistributionLock(String lockKey, int timeoutMsecs, int expireMsecs) {
        this(lockKey,timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }


    public boolean setLock() {
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                this.uuid = uuid;
                return commands.set(lockKey, uuid, "NX", "PX", expireMsecs);
            };
            RedisTemplate<String,Object> redisTemplate = ApplicationContextUtils.getBean("redisTemplate");
            String result = redisTemplate.execute(callback);

            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("【set redis occured an exception】", e);
        }
        return false;
    }

    public boolean tryLock() {
        try {
            long deadline = System.currentTimeMillis() + timeoutMsecs; //最后超时时间
            String result = null; //返回结果
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                this.uuid = uuid;
                return commands.set(lockKey, uuid, "NX", "PX", expireMsecs);
            };
            RedisTemplate<String,Object> redisTemplate = ApplicationContextUtils.getBean("redisTemplate");
            do {
                result = redisTemplate.execute(callback);
                Thread.sleep(1000L);
            }while( StringUtils.isEmpty(result) && System.currentTimeMillis() < deadline );//获取不到且未超时则重试

            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("set redis occured an exception", e);
        }
        return false;
    }

    public boolean releaseLock() {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(lockKey);
            List<String> args = new ArrayList<>();
            args.add(uuid);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            };
            RedisTemplate<String,Object> redisTemplate = ApplicationContextUtils.getBean("redisTemplate");
            Long result = redisTemplate.execute(callback);
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("【release lock occured an exception】", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }

}
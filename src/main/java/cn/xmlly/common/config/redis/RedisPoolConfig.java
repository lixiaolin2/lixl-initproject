package cn.xmlly.common.config.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author hwr
 * jedisPool的配置类
 */
@Slf4j
@Data
@CacheConfig
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisPoolConfig extends CachingConfigurerSupport {


    String host;

    Integer port;

    Integer timeout;

    String password;

    Integer database;

    @ConditionalOnMissingBean(name = "jedisPool")
    @Bean(name = "jedisPool")
    public JedisPool jedisPool(@Qualifier("jedisConfig") JedisPoolConfig jedisPoolConfig) {
        log.info("初始化……Redis Client==Host={},Port={},config={}", host, port, jedisPoolConfig);

        return new JedisPool(jedisPoolConfig, host, port, timeout,
                password, database);
    }

}

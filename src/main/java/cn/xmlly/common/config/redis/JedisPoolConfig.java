package cn.xmlly.common.config.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author hwr
 * jedisPoolConfig属性配置类
 */
@Slf4j
@Data
@Component
@ConfigurationProperties("jedis.pool")
public class JedisPoolConfig {
    private Integer maxIdle;

    private Integer minIdle;

    private Integer maxWait;

    private Integer maxTotal;

    private Integer maxInstruction;

    @ConditionalOnMissingBean(name = {"jedisConfig"})
    @Bean("jedisConfig")
    public redis.clients.jedis.JedisPoolConfig jedisPoolConfig() {
        redis.clients.jedis.JedisPoolConfig config = new redis.clients.jedis.JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);

        return config;
    }

}

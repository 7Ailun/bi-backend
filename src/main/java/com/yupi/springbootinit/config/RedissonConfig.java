package com.yupi.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @Author: 艾伦
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {
    private Integer database;
    private String host;
    private Integer port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s:%s",host,port);
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}

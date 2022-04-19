package org.spirits.aroma.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {
    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(RedisTestConfig.REDIS_CONNECTION);
        return Redisson.create(config);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

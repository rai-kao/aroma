package org.spirits.aroma.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import redis.embedded.RedisServer;

public class EmbeddedRedisInitializer {
    protected static RedisServer redisServer = null;

    @BeforeClass
    public static void startRedis() {
        if (redisServer == null) {
            redisServer = RedisServer.builder()
                    .port(RedisTestConfig.REDIS_PORT)
                    .bind(RedisTestConfig.REDIS_IP)
                    .setting("maxmemory 32M")
                    .build();
            redisServer.start();
        }
    }

    @AfterClass
    public static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
            redisServer = null;
        }
    }
}

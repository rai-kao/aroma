package org.spirits.aroma.redis;

public class RedisTestConfig {
    public static final String REDIS_IP = "127.0.0.1";

    public static final int REDIS_PORT = 36379;

    public static final String REDIS_CONNECTION = String.format("redis://%s:%s", REDIS_IP, REDIS_PORT);
}

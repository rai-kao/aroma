package org.spirits.aroma.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.spirits.aroma.redis.interceptor.InterceptorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class RedisCacheTest extends EmbeddedRedisInitializer {
    @Autowired
    public RedisTestService redisTestService;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void defaultCacheValueTest() throws Exception {
        // Store executed value
        int input = 1;
        redisTestService.defaultCacheKey(input);

        // Change redis cache value
        Integer expected = 2;
        Method method = RedisTestService.class.getMethod("defaultCacheKey", int.class);
        String key = InterceptorUtils.formatRedisKey(redisTestService, method, input);
        RBucket<Integer> bucket = redissonClient.getBucket(key);
        bucket.set(expected);

        // Get cached value
        Integer redisCacheValue = redisTestService.defaultCacheKey(input);
        assertEquals(expected, redisCacheValue);
    }

    @Test
    public void customCacheValueTest() throws Exception {
        // Store executed value
        int input = 1;
        redisTestService.customCacheKey(input);

        // Change redis cache value
        Integer expected = 2;
        Method method = RedisTestService.class.getMethod("customCacheKey", int.class);
        Object args = InterceptorUtils.simpleKey(redisTestService, method, input);
        String key = String.format("customCacheKey:%s", args);
        RBucket<Integer> bucket = redissonClient.getBucket(key);
        bucket.set(expected);

        // Get cached value
        Integer redisCacheValue = redisTestService.customCacheKey(input);
        assertEquals(expected, redisCacheValue);
    }
}

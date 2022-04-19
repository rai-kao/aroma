package org.spirits.aroma.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class RedisLockTest extends EmbeddedRedisInitializer {
    @Autowired
    public RedisTestService redisTestService;

    @Test
    public void defaultLockValueTest() {
        Map<Integer, Boolean> results = new ConcurrentHashMap<>();

        IntStream.range(0, 10).parallel().forEach(i -> {
            try {
                results.put(i, redisTestService.defaultLockKey());
            } catch (Exception e) {
                results.put(i, false);
            }
        });

        long success = results.values().stream().filter(i -> i).count();
        assertEquals(1, success);
    }

    @Test
    public void customLockValueTest() {
        Map<Integer, Boolean> results = new ConcurrentHashMap<>();

        IntStream.range(0, 10).parallel().forEach(i -> {
            try {
                results.put(i, redisTestService.customLockKey(0));
            } catch (Exception e) {
                results.put(i, false);
            }
        });

        long success = results.values().stream().filter(i -> i).count();
        assertEquals(1, success);
    }
}

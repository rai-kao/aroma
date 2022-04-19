package org.spirits.aroma.redis;

import org.awaitility.Awaitility;
import org.spirits.aroma.redis.annotation.RedisCache;
import org.spirits.aroma.redis.annotation.RedisLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTestService {
    @RedisCache
    public int defaultCacheKey(int input) {
        return input;
    }

    @RedisCache("customCacheKey:${input}")
    public int customCacheKey(int input) {
        return input;
    }

    @RedisLock
    public boolean defaultLockKey() throws InterruptedException {
        sleep(1, TimeUnit.SECONDS);
        return true;
    }

    @RedisLock("customLockKey:${input}")
    public boolean customLockKey(int input) throws InterruptedException {
        sleep(1, TimeUnit.SECONDS);
        return true;
    }

    private void sleep(long timeout, TimeUnit unit) {
        try {
            Awaitility.await().atMost(timeout, unit).until(() -> false);
        } catch (Exception e) {
            // Do nothing
        }
    }
}

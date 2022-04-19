package org.spirits.aroma.redis.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spirits.aroma.redis.annotation.RedisLock;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP method lock using Redisson client
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisLockInterceptor {
    private final Logger logger = LoggerFactory.getLogger(RedisLockInterceptor.class);

    private final RedissonClient redissonClient;

    public RedisLockInterceptor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(org.spirits.aroma.redis.annotation.RedisLock)")
    public Object redisTryLock(ProceedingJoinPoint joinPoint) throws Throwable {
        RLock lock = null;
        boolean res = false;
        String key = "not defined";
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
            RedisLock annotation = joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getAnnotation(RedisLock.class);
            key = InterceptorUtils.simpleRedisKey(joinPoint, RedisLock.class);

            lock = redissonClient.getLock(key);
            res = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.unit());

            if (!res) {
                throw new InterruptedException(String.format("Acquiring redis lock failed key: {%s}", key));
            }

            logger.trace("Acquired redis lock key: {{}}", key);
            return joinPoint.proceed();
        } finally {
            if (lock != null && res) {
                logger.trace("Unlock redis lock key: {{}}", key);
                lock.unlock();
            }
        }
    }
}

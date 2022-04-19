package org.spirits.aroma.redis.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spirits.aroma.redis.annotation.RedisCache;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * AOP method cache management using Redisson client
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedisCacheInterceptor {
    private final Logger logger = LoggerFactory.getLogger(RedisCacheInterceptor.class);

    private final RedissonClient redissonClient;

    public RedisCacheInterceptor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(org.spirits.aroma.redis.annotation.RedisCache)")
    public Object redisCache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisCache annotation = joinPoint.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(RedisCache.class);
        String key = InterceptorUtils.simpleRedisKey(joinPoint, RedisCache.class);

        RBucket<Object> cacheHit = redissonClient.getBucket(key);
        Object returnValue;
        if (cacheHit.get() != null) {
            logger.trace("Redis cache hit key: {{}}", key);
            return wrapCacheValue(method, cacheHit.get());
        } else {
            logger.trace("Redis cache miss key: {{}}", key);
            returnValue = joinPoint.proceed();
            cacheHit.set(unwrapReturnValue(returnValue), annotation.timeToLive(), annotation.unit());
            return returnValue;
        }
    }

    @Nullable
    private Object wrapCacheValue(Method method, @Nullable Object cacheValue) {
        if (method.getReturnType() == Optional.class &&
                (cacheValue == null || cacheValue.getClass() != Optional.class)) {
            return Optional.ofNullable(cacheValue);
        }
        return cacheValue;
    }

    @Nullable
    private Object unwrapReturnValue(@Nullable Object returnValue) {
        return ObjectUtils.unwrapOptional(returnValue);
    }
}

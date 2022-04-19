package org.spirits.aroma.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redis lock annotation provides method lock management.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * Redis lock key.
     */
    String value() default  "";

    /**
     * The maximum time to acquire the lock.
     * <p>Default is -1 (Don't wait)</p>
     */
    long waitTime() default -1L;

    /**
     * Lease time.
     * <p>Default is 60 seconds</p>
     */
    long leaseTime() default 60000L;

    /**
     * Time unit.
     * <p>Default is {@link TimeUnit#MILLISECONDS}</p>
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}


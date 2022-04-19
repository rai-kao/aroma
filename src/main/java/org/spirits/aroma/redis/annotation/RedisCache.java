package org.spirits.aroma.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redis cache annotation provides method cache management.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCache {
    /**
     * Custom Redis Key.
     */
    String value() default "";

    /**
     * Time to live interval.
     * <p>Default is 10 minutes</p>
     */
    long timeToLive() default 600000L;

    /**
     * Time unit.
     * <p>Default is {@link TimeUnit#MILLISECONDS}</p>
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}

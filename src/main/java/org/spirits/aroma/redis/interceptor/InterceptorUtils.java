package org.spirits.aroma.redis.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.function.SingletonSupplier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Common interceptor operations.
 */
public class InterceptorUtils {
    private static final SingletonSupplier<KeyGenerator> keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);

    private InterceptorUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Extract arguments to flat object array.
     *
     * @param method target method
     * @param args   original arguments
     * @return flat argument array
     */
    public static Object[] extractArgs(Method method, Object[] args) {
        if (!method.isVarArgs()) {
            return args;
        }
        Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
        Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
        System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
        System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
        return combinedArgs;
    }

    /**
     * Generate a map of argument names and values
     *
     * @param method target method
     * @param names  argument names
     * @param args   argument values
     * @return map of arguments
     */
    public static Map<String, Object> argsToMap(Method method, String[] names, Object[] args) {
        if (!method.isVarArgs()) {
            return IntStream.range(0, names.length).boxed().collect(Collectors.toMap(i -> names[i], i -> args[i]));
        }

        int varArgIndex = names.length - 1;
        Object[] flatArgs = extractArgs(method, args);
        Map<String, Object> map = IntStream.range(0, varArgIndex).boxed().collect(Collectors.toMap(i -> names[i], i -> flatArgs[i]));

        for (int i = varArgIndex; i < flatArgs.length; i++) {
            map.put(names[varArgIndex] + (i - varArgIndex), flatArgs[i]);
        }
        return map;
    }

    /**
     * Format simple key with variables
     *
     * @param joinPoint target aspect join point
     * @param type      annotation type
     * @param <T>       annotation
     * @return simple argument key
     * @throws NoSuchMethodException if method is not found
     */
    public static <T extends Annotation> String simpleRedisKey(JoinPoint joinPoint, Class<T> type) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        T annotation = joinPoint.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(type);
        String value = getAnnotationValue(annotation, type);

        if (StringUtils.isNoneBlank(value)) {
            Map<String, Object> argsMap = argsToMap(method, signature.getParameterNames(), joinPoint.getArgs());
            return new StringSubstitutor(argsMap).replace(value);
        } else {
            Object[] args = extractArgs(method, joinPoint.getArgs());
            return formatRedisKey(joinPoint.getTarget(), method, args);
        }
    }

    /**
     * Simple key generator
     *
     * @param target the target instance
     * @param method the method being called
     * @param args   the method parameters (with any var-args expanded)
     * @return a generated key
     */
    public static Object simpleKey(Object target, Method method, Object... args) {
        return keyGenerator.obtain().generate(target, method, args);
    }

    /**
     * Compose method and arguments to a Redis key
     *
     * @param target the target instance
     * @param method the method being called
     * @param args   the method parameters (with any var-args expanded)
     * @return a formatted key
     */
    public static String formatRedisKey(Object target, Method method, Object... args) {
        Object simpleKey = simpleKey(target, method, args);
        return String.format("%s:%s", method, simpleKey);
    }

    /**
     * Get Redis annotation value property.
     *
     * @param annotation target annotation
     * @param type       annotation type
     * @param <T>        annotation class
     * @return value property
     * @throws NoSuchMethodException if value method is not found or failed to invoke.
     */
    public static <T extends Annotation> String getAnnotationValue(T annotation, Class<T> type) throws NoSuchMethodException {
        try {
            Method valueMethod = type.getMethod("value");
            return (String) valueMethod.invoke(annotation);
        } catch (Exception e) {
            throw new NoSuchMethodException(e.getMessage());
        }
    }
}

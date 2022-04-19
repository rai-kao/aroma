# Aroma :herb:

Lightweight utilities focus on AOP annotations and wrapper methods.

## Redis utilities

Redis utilities use [Reddison](https://github.com/redisson/redisson) client features for implementation.

### Redis Lock

- Default usage:
  > Use method signature and all arguments for Redis lock key.
  ```java
  @RedisLock
  public void method(Object arg) {
  ...
  }
  ```
- Custom key
  > Use `${argument name}` for variable replacement
  ```java
  @RedisLock("method:${arg1}")
  public void method(Object arg0, Object arg1) {
  ...
  }
  ```
- Example</br>
  > Resource
  ```java
  @RedisLock("mutex:${resource}")
  public void mutualExclusion(String resource) throws InterruptedException {
  ...
  }
  ```
  
  > Service
  ```java
  ...
  try {
    resource.mutualExclusion("r0")
  } catch (InterruptedException e) {
    // Handle method not been executed here
  }  
  ...
  ```

  *Please note that if calling `resource.mutualExclusion("r0")` and `resource.mutualExclusion("r1")`,
  they can execute concurrently without problem.*

### Redis Cache

- Default usage:
  > Use method signature and all arguments for Redis lock key.
  ```java
  @RedisCache
  public void method(Object arg) {
  ...
  }
  ```
- Custom key
  > Use `${argument name}` for variable replacement
  ```java
  @RedisCache("method:${arg1}")
  public void method(Object arg0, Object arg1) {
  ...
  }
  ```
- Setup time to live
  ```java
  @RedisCache(timeToLive = 30, unit = TimeUnit.MINUTES)
  public void method(Object arg) {
  ...
  }
  ```
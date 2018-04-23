package com.caseyscarborough.spring.redis.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@Configuration
@EnableCaching
@PropertySource("classpath:redis.properties")
public class CacheConfig extends CachingConfigurerSupport {

  private static final Logger log = Logger.getLogger(CacheConfig.class);

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;

  @Value("${redis.expiration.timeout}")
  private int expirationTimeout;

  public static final String CACHE_NAME = "test1";

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
    redisConnectionFactory.setHostName(host);
    redisConnectionFactory.setPort(port);
    return redisConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cf);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
    cacheManager.setDefaultExpiration(expirationTimeout);
    cacheManager.setCacheNames(Arrays.asList(CACHE_NAME));
    return cacheManager;
  }

  @Bean
  public KeyGenerator customKeyGenerator() {
    return (o, method, objects) -> {
      StringBuilder sb = new StringBuilder();
      sb.append(o.getClass().getName());
      sb.append(method.getName());
      for (Object obj : objects) {
        sb.append(obj.toString());
      }
      return sb.toString();
    };
  }

}

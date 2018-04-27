package com.caseyscarborough.spring.redis.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
@PropertySource("classpath:redis.properties")
public class CacheConfig extends CachingConfigurerSupport {

  public static final String CACHE_NAME = "test1";

  private static final Logger LOGGER = Logger.getLogger(CacheConfig.class);

  @Value("${redis.host?:localhost}")
  private String host;

  @Value("${redis.port}")
  private int port;

  @Value("${redis.password}")
  private String password;

  @Value("${redis.expiration.timeout}")
  private int expirationTimeout;

  @Value("${redis.cluster.max-redirects}")
  private int maxRedirects;

  @Value("${redis.cluster.nodes}")
  private String hosts;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(50); // maximum active connections
    poolConfig.setMaxIdle(20);
    RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
    redisClusterConfiguration.setClusterNodes(parseHosts());
    redisClusterConfiguration.setMaxRedirects(maxRedirects);
    JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
    redisConnectionFactory.setUsePool(true);
    return redisConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(cf);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisTemplate) {
    RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
    cacheManager.setDefaultExpiration(expirationTimeout);
    cacheManager.setCacheNames(Arrays.asList(CACHE_NAME));
    return cacheManager;
  }

  private Iterable<RedisNode> parseHosts() {
    return Arrays.stream(hosts.split(",")).map(host -> {
      String[] hostInfo = host.split(":");
      String hostName = hostInfo[0].trim();
      Integer port = Integer.parseInt(hostInfo[1].trim());
      return new RedisNode(hostName, port);
    }).collect(Collectors.toList());
  }

}

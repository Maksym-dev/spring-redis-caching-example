package com.caseyscarborough.spring.redis.repository;

import com.caseyscarborough.spring.redis.domain.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import static com.caseyscarborough.spring.redis.config.CacheConfig.CACHE_NAME;

@Component
public class UserRepositoryImpl implements UserRepository {

  private static final Logger log = Logger.getLogger(UserRepositoryImpl.class);

  @Autowired
  private CacheManager cacheManager;

  /**
   * The User that is saved from this method will be stored in the
   * cache and referenced by its' ID.
   */
  @Override
  public User saveUser(Long id) {
    log.debug("Saving user...");
    User user = new User();
    user.setId(id);
    cacheManager.getCache(CACHE_NAME).put(id.toString(), user);
    return user;
  }

  @Override
  public User getUser(Long id) {
    log.debug("Retrieving user...");
    return cacheManager.getCache(CACHE_NAME).get(id.toString(), User.class);
  }

  /**
   * When this method is called, the cached User will be deleted from
   * the cache.
   */
  @Override
  public void deleteUser(Long id) {
    log.debug("Deleting user...");
    cacheManager.getCache(CACHE_NAME).evict(id.toString());
  }
}

package com.caseyscarborough.spring.redis.repository;

import com.caseyscarborough.spring.redis.domain.User;

public interface UserRepository {

  User saveUser(Long id);

  User getUser(Long id);

  void deleteUser(Long id);
}
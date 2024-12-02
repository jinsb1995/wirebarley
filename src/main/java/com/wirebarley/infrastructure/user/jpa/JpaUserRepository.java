package com.wirebarley.infrastructure.user.jpa;

import com.wirebarley.infrastructure.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
}

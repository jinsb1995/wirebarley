package com.wirebarley.infrastructure.account.jpa;

import com.wirebarley.infrastructure.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findTopAccountNumberByOrderByIdDesc();

    @Query("select a from AccountEntity a join fetch a.user where a.id = :id")
    Optional<AccountEntity> findByIdWithUser(@Param("id") Long id);
}

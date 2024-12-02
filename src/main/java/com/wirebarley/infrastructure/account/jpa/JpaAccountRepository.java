package com.wirebarley.infrastructure.account.jpa;

import com.wirebarley.infrastructure.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findTopAccountNumberByOrderByIdDesc();
}

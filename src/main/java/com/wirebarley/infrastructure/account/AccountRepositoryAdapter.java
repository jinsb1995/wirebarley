package com.wirebarley.infrastructure.account;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.account.entity.AccountEntity;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.exception.CustomException;
import com.wirebarley.infrastructure.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.wirebarley.infrastructure.exception.ExceptionConstant.ACCOUNT_NOT_FOUND_EXCEPTION;

@RequiredArgsConstructor
@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Long findLatestAccountNumber() {
        AccountEntity latestAccount = jpaAccountRepository.findTopAccountNumberByOrderByIdDesc();
        return latestAccount == null ? null : latestAccount.getAccountNumber();
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = AccountEntity.create(account);
        return jpaAccountRepository.save(accountEntity).toDomain();
    }

    @Override
    public Account findById(Long accountId) {
        return jpaAccountRepository.findByIdWithUser(accountId)
                .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND_EXCEPTION.getMessage()))
                .toDomain();
    }

    @Override
    public void deleteById(Long accountId) {
        jpaAccountRepository.deleteById(accountId);
    }

    @Override
    public Account findByAccountNumber(Long accountNumber) {
        return jpaAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND_EXCEPTION.getMessage()))
                .toDomain();
    }
}

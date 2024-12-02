package com.wirebarley.infrastructure.account;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.account.entity.AccountEntity;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        User user = account.getUser();
        UserEntity userEntity = UserEntity.of(user);
        AccountEntity accountEntity = AccountEntity.create(account, userEntity);
        return jpaAccountRepository.save(accountEntity).toDomain();
    }

    @Override
    public Account findById(Long accountId) {
        return jpaAccountRepository.findById(accountId)
                .orElseThrow()
                .toDomain();
    }

    @Override
    public void deleteById(Long accountId) {
        jpaAccountRepository.deleteById(accountId);
    }
}

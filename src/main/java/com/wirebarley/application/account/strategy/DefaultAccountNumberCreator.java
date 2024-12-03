package com.wirebarley.application.account.strategy;

import com.wirebarley.application.account.AccountNumberCreator;
import com.wirebarley.domain.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultAccountNumberCreator implements AccountNumberCreator {

    private static final long INITIAL_ACCOUNT_NUMBER = 1111L;

    private final AccountRepository accountRepository;

    @Override
    public Long create() {

        Long latestAccountNumber = accountRepository.findLatestAccountNumber();
        if (latestAccountNumber == null) {
            return INITIAL_ACCOUNT_NUMBER;
        }

        return latestAccountNumber + 1;
    }
}

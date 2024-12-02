package com.wirebarley.domain.account;

public interface AccountRepository {
    Long findLatestAccountNumber();

    Account save(Account account);
}

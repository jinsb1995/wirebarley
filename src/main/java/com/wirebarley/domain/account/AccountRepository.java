package com.wirebarley.domain.account;

public interface AccountRepository {
    Long findLatestAccountNumber();

    Account save(Account account);

    Account findById(Long accountId);

    void deleteById(Long accountId);

    Account findByAccountNumber(Long accountNumber);
}

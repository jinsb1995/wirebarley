package com.wirebarley.fixture;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.user.User;

import java.time.LocalDateTime;

public class AccountFixture {

    public static Account createAccountFixture(long accountNumber, User savedUser) {

        return Account.builder()
                .accountNumber(accountNumber)
                .password(1234)
                .balance(1000L)
                .user(savedUser)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
    }
}

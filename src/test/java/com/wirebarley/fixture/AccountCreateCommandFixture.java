package com.wirebarley.fixture;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;

public class AccountCreateCommandFixture {

    public static AccountCreateCommand createCommandFixture(Integer password, Long balance, Long userId) {

        AccountCreateCommand.AccountCreateCommandBuilder builder = AccountCreateCommand.builder();
        if (password != null) {
            builder.password(password);
        }

        if (balance != null) {
            builder.balance(balance);
        }

        if (userId != null) {
            builder.userId(userId);
        }

        return builder.build();
    }
}

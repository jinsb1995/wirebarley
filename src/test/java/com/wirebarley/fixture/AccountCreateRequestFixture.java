package com.wirebarley.fixture;

import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;

public class AccountCreateRequestFixture {

    public static AccountCreateRequest createRequestFixture(Integer password, Long balance, Long userId) {

        AccountCreateRequest.AccountCreateRequestBuilder builder = AccountCreateRequest.builder();
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

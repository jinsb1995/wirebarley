package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AccountService {

    private static final long INITIAL_ACCOUNT_NUMBER = 1111L;

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse createAccount(AccountCreateCommand command, LocalDateTime registeredAt) {
        User findUser = userRepository.findById(command.userId());

        Long newAccountNumber = createNewAccountNumber();

        Account account = command.toDomain(findUser, newAccountNumber, registeredAt);

        Account save = accountRepository.save(account);
        return AccountResponse.of(save);
    }

    private Long createNewAccountNumber() {
        Long latestAccountNumber = accountRepository.findLatestAccountNumber();
        if (latestAccountNumber == null) {
            return INITIAL_ACCOUNT_NUMBER;
        }

        return latestAccountNumber + 1;
    }

}

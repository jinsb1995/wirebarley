package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberCreator accountNumberCreator;

    @Transactional
    public AccountResponse createAccount(AccountCreateCommand command, LocalDateTime registeredAt) {
        User findUser = userRepository.findById(command.userId());

        Long newAccountNumber = accountNumberCreator.create();

        Account account = command.toDomain(findUser, newAccountNumber, registeredAt);

        Account save = accountRepository.save(account);
        return AccountResponse.of(save);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        Account findAccount = accountRepository.findById(accountId);

        findAccount.checkOwner(userId);

        accountRepository.deleteById(accountId);
    }
}

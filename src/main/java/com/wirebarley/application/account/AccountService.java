package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.request.TransferCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.application.account.dto.response.TransactionResponse;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.wirebarley.infrastructure.exception.ExceptionConstant.SAME_ACCOUNT_EXCEPTION;
import static com.wirebarley.infrastructure.exception.ExceptionConstant.ZERO_AMOUNT_EXCEPTION;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberCreator accountNumberCreator;
    private final TransactionRepository transactionRepository;
    private final TransferLimitChecker transferLimitChecker;

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

    @Transactional
    public TransactionResponse transfer(TransferCommand command) {

        if (isSameAccount(command.withdrawNumber(), command.depositNumber())) {
            throw new CustomException(SAME_ACCOUNT_EXCEPTION.getMessage());
        }

        if (command.amount() <= 0L) {
            throw new CustomException(ZERO_AMOUNT_EXCEPTION.getMessage());
        }

        Account withdrawAccount = accountRepository.findByAccountNumber(command.withdrawNumber());
        Account depositAccount = accountRepository.findByAccountNumber(command.depositNumber());

        Long transferCharge = withdrawAccount.getTransferCharge(command.amount());
        withdrawAccount.checkOwner(command.userId());
        withdrawAccount.checkPassword(command.accountPassword());
        withdrawAccount.checkEnoughBalanceByCharge(command.amount(), transferCharge);

        transferLimitChecker.checkTransferLimitByPeriod(withdrawAccount.getAccountNumber(), command.amount(), command.transferDate());

        withdrawAccount.withdraw(command.amount(), transferCharge);
        depositAccount.deposit(command.amount());

        Transaction transaction = Transaction.builder()
                .withdrawAccountNumber(withdrawAccount.getAccountNumber())
                .depositAccountNumber(depositAccount.getAccountNumber())
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .amount(command.amount())
                .type(TransactionType.TRANSFER)
                .sender(withdrawAccount.getUser().getUsername())
                .receiver(depositAccount.getUser().getUsername())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponse.of(savedTransaction);
    }

    private boolean isSameAccount(Long withdrawNumber, Long depositNumber) {
        return withdrawNumber.equals(depositNumber);
    }
}

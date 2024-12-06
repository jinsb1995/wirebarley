package com.wirebarley.application.account;

import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.infrastructure.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static com.wirebarley.infrastructure.exception.ExceptionConstant.*;

@RequiredArgsConstructor
@Component
public class TransferLimitChecker {

    private static final Long DAILY_LIMIT = 100_000L;
    private static final Long WEEKLY_LIMIT = 500_000L;
    private static final Long MONTHLY_LIMIT = 2_000_000L;

    private final TransactionRepository transactionRepository;

    public void checkTransferLimitByPeriod(Long accountNumber, Long amount, LocalDateTime transferDate) {

        List<TransactionType> withdrawalTypes = TransactionType.getWithdrawalTypes();
        LocalDateTime dayStart = transferDate.toLocalDate().atStartOfDay();
        Long dailyTotalAmount = transactionRepository.findTotalWithdrawalAmountByWithdrawAccount(accountNumber, dayStart, transferDate, withdrawalTypes);
        long dailyTotal = dailyTotalAmount + amount;
        if (dailyTotal > DAILY_LIMIT) {
            throw new CustomException(DAILY_LIMIT_EXCEPTION.getMessage());
        }

        LocalDateTime weekStart = transferDate.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        Long weeklyTotalAmount = transactionRepository.findTotalWithdrawalAmountByWithdrawAccount(accountNumber, weekStart, transferDate, withdrawalTypes);
        long weeklyTotal = weeklyTotalAmount + amount;
        if (weeklyTotal > WEEKLY_LIMIT) {
            throw new CustomException(WEEKLY_LIMIT_EXCEPTION.getMessage());
        }

        LocalDateTime monthStart = transferDate.toLocalDate().withDayOfMonth(1).atStartOfDay();
        Long monthlyTotalAmount = transactionRepository.findTotalWithdrawalAmountByWithdrawAccount(accountNumber, monthStart, transferDate, withdrawalTypes);
        long monthlyTotal = monthlyTotalAmount + amount;
        if (monthlyTotal > MONTHLY_LIMIT) {
            throw new CustomException(MONTHLY_LIMIT_EXCEPTION.getMessage());
        }
    }
}

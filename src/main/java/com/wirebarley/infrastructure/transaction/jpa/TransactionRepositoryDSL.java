package com.wirebarley.infrastructure.transaction.jpa;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarley.domain.transaction.dto.TransactionRetrieveQuery;
import com.wirebarley.infrastructure.account.entity.QAccountEntity;
import com.wirebarley.infrastructure.transaction.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wirebarley.domain.transaction.TransactionType.DEPOSIT;
import static com.wirebarley.domain.transaction.TransactionType.WITHDRAW;
import static com.wirebarley.infrastructure.transaction.entity.QTransactionEntity.transactionEntity;

@RequiredArgsConstructor
@Repository
public class TransactionRepositoryDSL {

    private final JPAQueryFactory query;


    public List<TransactionEntity> findTransactions(TransactionRetrieveQuery dto) {
        QAccountEntity withdrawAccount = new QAccountEntity("withdrawAccount");
        QAccountEntity depositAccount = new QAccountEntity("depositAccount");

        JPAQuery<TransactionEntity> from = query
                .select(transactionEntity)
                .from(transactionEntity);

        if (dto.type() == WITHDRAW) {
            from
                    .innerJoin(transactionEntity.withdrawAccount, withdrawAccount).fetchJoin()
                    .where(
                            transactionEntity.withdrawAccount.id.eq(dto.accountId())
                                    .and(transactionEntity.type.eq(dto.type()))
                    );
        } else if (dto.type() == DEPOSIT) {
            from
                    .innerJoin(transactionEntity.depositAccount, depositAccount).fetchJoin()
                    .where(
                            transactionEntity.depositAccount.id.eq(dto.accountId())
                                    .and(transactionEntity.type.eq(dto.type()))
                    );
        } else {
            from
                    .leftJoin(transactionEntity.withdrawAccount, withdrawAccount).fetchJoin()
                    .leftJoin(transactionEntity.depositAccount, depositAccount).fetchJoin()
                    .where(
                            withdrawAccountIdEq(dto.accountId())
                                    .or(depositAccountIdEq(dto.accountId()))
                    );
        }

        return from
                .orderBy(transactionEntity.createdAt.desc())
                .offset(dto.offset())
                .limit(dto.count())
                .fetch();
    }

    private BooleanExpression withdrawAccountIdEq(Long accountId) {
        return transactionEntity.withdrawAccount.id.eq(accountId);
    }

    private BooleanExpression depositAccountIdEq(Long accountId) {
        return transactionEntity.depositAccount.id.eq(accountId);
    }
}

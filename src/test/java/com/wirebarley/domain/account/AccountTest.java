package com.wirebarley.domain.account;

import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @DisplayName("계좌가 본인 소유가 아니면 예외가 발생한다.")
    @Test
    public void checkOwner() {
        // given
        Long wrongUserId = 2L;
        User user = User.builder().id(1L).build();
        Account account = Account.builder().user(user).build();

        // when
        // then
        assertThatThrownBy(() -> account.checkOwner(wrongUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 소유자가 아닙니다.");
    }

    @DisplayName("출금계좌의 비밀번호가 다르면 예외가 발생한다.")
    @Test
    public void checkPassword() {
        // given
        int wrongPassword = 5678;
        Account account = Account.builder().password(1234).build();

        // when
        // then
        assertThatThrownBy(() -> account.checkPassword(wrongPassword))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 비밀번호 검증에 실패했습니다.");
    }

    @DisplayName("이체하려는 금액의 수수료를 구한다.")
    @Test
    public void getTransferCharge() {
        // given
        long balance = 10000;
        Account account = Account.builder().build();

        // when
        Long transferCharge = account.getTransferCharge(balance);

        // then
        assertThat(transferCharge).isEqualTo(100);
    }

    @DisplayName("출금하려는 계좌의 잔액이 출금하려는 금액보다 적으면 예외가 발생한다.")
    @Test
    public void checkEnoughBalanceByCharge() {
        // given
        long balance = 10000L;
        Account account = Account.builder().balance(100L).build();
        Long transferCharge = account.getTransferCharge(balance);

        // when
        // then
        assertThatThrownBy(() -> account.checkEnoughBalanceByCharge(balance, transferCharge))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 잔액이 부족합니다.");
    }

    @DisplayName("출금하려는 계좌의 잔액이 출금하려는 금액보다 적으면 예외가 발생한다.")
    @Test
    public void withdraw() {
        // given
        long balance = 10000L;
        Account account = Account.builder().balance(100L).build();
        Long transferCharge = account.getTransferCharge(balance);

        // when
        // then
        assertThatThrownBy(() -> account.withdraw(balance, transferCharge))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 잔액이 부족합니다.");
    }

    @DisplayName("출금하려는 계좌의 잔액이 출금하려는 금액만큼 있어도 수수료를 낼 돈이 없으면 안된다.")
    @Test
    public void withdraw2() {
        // given
        long balance = 10000L;
        Account account = Account.builder().balance(10000L).build();
        Long transferCharge = account.getTransferCharge(balance);

        // when
        // then
        assertThatThrownBy(() -> account.withdraw(balance, transferCharge))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 잔액이 부족합니다.");
    }
}
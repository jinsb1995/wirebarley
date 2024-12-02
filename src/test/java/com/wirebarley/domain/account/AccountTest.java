package com.wirebarley.domain.account;

import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @DisplayName("계좌를 삭제할 때 본인 소유가 아니면 예외가 발생한다.")
    @Test
    public void checkOwner() {
        // given
        Long wrongUserId = 1111L;

        User user = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        Account account = Account.builder()
                .id(1L)
                .accountNumber(1111L)
                .user(user)
                .build();

        // when
        // then
        assertThatThrownBy(() -> account.checkOwner(wrongUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 소유자가 아닙니다.");
    }
}
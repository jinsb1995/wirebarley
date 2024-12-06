package com.wirebarley.restdocs.docs;

import com.wirebarley.restdocs.extension.ApiValueTypes;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;

import static com.wirebarley.restdocs.extension.ApiValueTypes.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class AccountDocs {

    public Snippet createAccountRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet createAccountRequestBody() {

        return relaxedRequestFields(
                fieldWithPath("password").type(INTEGER).description("계좌 비밀번호"),
                fieldWithPath("balance").type(LONG).description("계좌 생성 시 입금할 금액").optional(),
                fieldWithPath("userId").type(LONG).description("회원 식별자")
        );
    }

    public Snippet createAccountResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet createAccountResponseBody() {
        return responseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data.id").type(LONG).description("계좌 식별자"),
                fieldWithPath("data.accountNumber").type(LONG).description("새로 생성된 계좌번호"),
                fieldWithPath("data.balance").type(LONG).description("계좌 잔액"),
                fieldWithPath("data.user.id").type(LONG).description("유저 식별자"),
                fieldWithPath("data.user.username").type(STRING).description("유저 이름"),
                fieldWithPath("data.user.email").type(STRING).description("유저 이메일"),
                fieldWithPath("data.user.createdAt").type(DATETIME_STRING).description("생성일"),
                fieldWithPath("data.user.modifiedAt").type(DATETIME_STRING).description("수정일"),
                fieldWithPath("data.registeredAt").type(DATETIME_STRING).description("계좌 등록일"),
                fieldWithPath("data.unregisteredAt").type(DATETIME_STRING).description("계좌 해지일"),
                fieldWithPath("data.createdAt").type(DATETIME_STRING).description("계좌 생성일"),
                fieldWithPath("data.modifiedAt").type(DATETIME_STRING).description("최종 수정일")
        );
    }


    public Snippet deleteAccountRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet deleteAccountPathVariable() {
        return pathParameters(
                parameterWithName("id").attributes(getDataType(LONG)).description("계좌 식별자")
        );
    }

    public Snippet deleteAccountRequestParam() {

        return queryParameters(
                parameterWithName("userId").attributes(getDataType(LONG)).description("회원 식별자")
        );
    }

    public Snippet deleteAccountResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet deleteAccountResponseBody() {
        return relaxedResponseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data").type(STRING).description("응답 데이터")
        );
    }


    public Snippet depositRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet depositRequestBody() {

        return requestFields(
                fieldWithPath("accountNumber").type(LONG).description("입금 계좌번호"),
                fieldWithPath("amount").type(LONG).description("입금액"),
                fieldWithPath("sender").type(STRING).description("송금인")
        );
    }

    public Snippet depositResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet depositResponseBody() {
        return responseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data.id").type(LONG).description("거래내역 식별자"),
                fieldWithPath("data.depositAccountNumber").type(LONG).description("입금 계좌번호"),
                fieldWithPath("data.amount").type(LONG).description("입금액"),
                fieldWithPath("data.depositAccountBalance").type(LONG).description("입금 후 잔액"),
                fieldWithPath("data.type").type(STRING).description("거래 종류"),
                fieldWithPath("data.sender").type(STRING).description("송금인"),
                fieldWithPath("data.receiver").type(STRING).description("수취인"),
                fieldWithPath("data.createdAt").type(DATETIME_STRING).description("계좌 생성일"),
                fieldWithPath("data.modifiedAt").type(DATETIME_STRING).description("최종 수정일")
        );
    }


    public Snippet withdrawRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet withdrawRequestBody() {

        return requestFields(
                fieldWithPath("accountNumber").type(LONG).description("출금 계좌번호"),
                fieldWithPath("amount").type(LONG).description("출금액"),
                fieldWithPath("userId").type(LONG).description("출금 계좌 주인 정보"),
                fieldWithPath("password").type(INTEGER).description("출금 계좌 비밀번호"),
                fieldWithPath("receiver").type(STRING).description("수취인")
        );
    }

    public Snippet withdrawResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet withdrawResponseBody() {
        return responseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data.id").type(LONG).description("거래내역 식별자"),
                fieldWithPath("data.withdrawAccountNumber").type(LONG).description("출금 계좌번호"),
                fieldWithPath("data.amount").type(LONG).description("입금액"),
                fieldWithPath("data.withdrawAccountBalance").type(LONG).description("출금 후 잔액"),
                fieldWithPath("data.type").type(STRING).description("거래 종류"),
                fieldWithPath("data.sender").type(STRING).description("송금인"),
                fieldWithPath("data.receiver").type(STRING).description("수취인"),
                fieldWithPath("data.createdAt").type(DATETIME_STRING).description("계좌 생성일"),
                fieldWithPath("data.modifiedAt").type(DATETIME_STRING).description("최종 수정일")
        );
    }


    public Snippet transferRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet transferRequestBody() {

        return requestFields(
                fieldWithPath("withdrawNumber").type(LONG).description("출금 계좌번호"),
                fieldWithPath("depositNumber").type(LONG).description("이체할 계좌번호"),
                fieldWithPath("userId").type(LONG).description("출금 계좌 주인 정보"),
                fieldWithPath("amount").type(LONG).description("출금액"),
                fieldWithPath("accountPassword").type(INTEGER).description("출금 계좌 비밀번호"),
                fieldWithPath("transferDate").type(DATETIME_STRING).description("이체 날짜").optional()
        );
    }

    public Snippet transferResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet transferResponseBody() {
        return responseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data.id").type(LONG).description("거래내역 식별자"),
                fieldWithPath("data.withdrawAccountNumber").type(LONG).description("출금 계좌번호").optional(),
                fieldWithPath("data.depositAccountNumber").type(LONG).description("입금 계좌번호").optional(),
                fieldWithPath("data.amount").type(LONG).description("입금액"),
                fieldWithPath("data.withdrawAccountBalance").type(LONG).description("출금 후 잔액").optional(),
                fieldWithPath("data.depositAccountBalance").type(LONG).description("입금 후 잔액").optional(),
                fieldWithPath("data.type").type(STRING).description("거래 종류"),
                fieldWithPath("data.sender").type(STRING).description("송금인"),
                fieldWithPath("data.receiver").type(STRING).description("수취인"),
                fieldWithPath("data.createdAt").type(DATETIME_STRING).description("계좌 생성일"),
                fieldWithPath("data.modifiedAt").type(DATETIME_STRING).description("최종 수정일")
        );
    }

    private Attributes.Attribute getDataType(ApiValueTypes apiValueTypes) {
        return new Attributes.Attribute("type", apiValueTypes);
    }
}

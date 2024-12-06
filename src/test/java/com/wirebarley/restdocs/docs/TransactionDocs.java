package com.wirebarley.restdocs.docs;

import com.wirebarley.restdocs.extension.ApiValueTypes;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;

import static com.wirebarley.restdocs.extension.ApiValueTypes.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedQueryParameters;

public class TransactionDocs {

    public Snippet retrieveTransactionRequestHeader() {
        return requestHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet retrieveTransactionRequestParam() {

        return relaxedQueryParameters(
                parameterWithName("offset").attributes(getDataType(INTEGER)).description("페이지 시작 번호"),
                parameterWithName("count").attributes(getDataType(INTEGER)).description("한 페이지에 보여줄 개수"),
                parameterWithName("userId").attributes(getDataType(LONG)).description("회원 식별자"),
                parameterWithName("accountId").attributes(getDataType(LONG)).description("계좌 식별자"),
                parameterWithName("type").attributes(getDataType(STRING)).description("조회하려는 거래 종류").optional()
        );
    }

    public Snippet retrieveTransactionResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet retrieveTransactionResponseBody() {
        return relaxedResponseFields(
                fieldWithPath("code").type(INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(LONG).description("응답 상태"),
                fieldWithPath("message").type(LONG).description("응답 메시지"),
                fieldWithPath("data[].id").type(LONG.toArray()).description("거래내역 식별자"),
                fieldWithPath("data[].withdrawAccountNumber").type(LONG.toArray()).description("출금 계좌번호").optional(),
                fieldWithPath("data[].depositAccountNumber").type(LONG.toArray()).description("입금 계좌번호").optional(),
                fieldWithPath("data[].amount").type(LONG.toArray()).description("입금액"),
                fieldWithPath("data[].withdrawAccountBalance").type(LONG.toArray()).description("출금 후 잔액").optional(),
                fieldWithPath("data[].depositAccountBalance").type(LONG.toArray()).description("입금 후 잔액").optional(),
                fieldWithPath("data[].type").type(STRING.toArray()).description("거래 종류"),
                fieldWithPath("data[].sender").type(STRING.toArray()).description("송금인"),
                fieldWithPath("data[].receiver").type(STRING.toArray()).description("수취인"),
                fieldWithPath("data[].createdAt").type(DATETIME_STRING.toArray()).description("계좌 생성일"),
                fieldWithPath("data[].modifiedAt").type(DATETIME_STRING.toArray()).description("최종 수정일")
        );
    }

    private Attributes.Attribute getDataType(ApiValueTypes apiValueTypes) {
        return new Attributes.Attribute("type", apiValueTypes);
    }
}

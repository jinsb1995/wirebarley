package com.wirebarley.restdocs.docs;

import com.wirebarley.restdocs.extension.ApiValueTypes;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;

public class UserDocs {

    public Snippet retrieveUsersResponseHeader() {
        return responseHeaders(
                headerWithName("Content-Type").description("application/json")
        );
    }

    public Snippet retrieveUsersResponseBody() {
        return relaxedResponseFields(
                fieldWithPath("code").type(ApiValueTypes.INTEGER).description("응답 상태 코드"),
                fieldWithPath("status").type(ApiValueTypes.LONG).description("응답 상태"),
                fieldWithPath("message").type(ApiValueTypes.LONG).description("응답 메시지"),
                fieldWithPath("data[].id").type(ApiValueTypes.LONG).description("유저 식별자"),
                fieldWithPath("data[].username").type(ApiValueTypes.STRING).description("유저 이름"),
                fieldWithPath("data[].email").type(ApiValueTypes.STRING).description("유저 이메일"),
                fieldWithPath("data[].createdAt").type(ApiValueTypes.DATETIME_STRING).description("생성일"),
                fieldWithPath("data[].modifiedAt").type(ApiValueTypes.DATETIME_STRING).description("수정일")
        );
    }
}

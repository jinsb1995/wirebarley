package com.wirebarley.restdocs;

import com.wirebarley.application.user.UserService;
import com.wirebarley.application.user.dto.response.UserResponse;
import com.wirebarley.domain.user.User;
import com.wirebarley.restdocs.docs.UserDocs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class UserDocumentTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void retrieveUsers() throws Exception {
        UserDocs snippets = new UserDocs();

        // given
        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        // when
        given(userService.retrieveUsers()).willReturn(List.of(UserResponse.of(user)));

        // then
        mvc.perform(
                        get("/api/v1/users")
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "retrieve-users",
                                snippets.retrieveUsersResponseHeader(),
                                snippets.retrieveUsersResponseBody()
                        )
                );

    }
}

package com.wirebarley.infrastructure.config;

import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SqlInitConfig {

    private final UserRepository userRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void init(ContextRefreshedEvent event) {
        User user1 = User.builder()
                .email("kim@email.com")
                .username("김김김")
                .password("password")
                .build();
        User user2 = User.builder()
                .email("lee@email.com")
                .username("이이이")
                .password("password")
                .build();
        User user3 = User.builder()
                .email("bae@email.com")
                .username("배배배")
                .password("password")
                .build();
        userRepository.saveAll(List.of(user1, user2, user3));
    }
}

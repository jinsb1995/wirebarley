package com.wirebarley.infrastructure.user;

import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.exception.CustomException;
import com.wirebarley.infrastructure.user.entity.UserEntity;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wirebarley.infrastructure.exception.ExceptionConstant.USER_NOT_FOUND_EXCEPTION;

@RequiredArgsConstructor
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        UserEntity userEntity = UserEntity.create(user);
        return jpaUserRepository.save(userEntity).toDomain();
    }

    @Override
    public List<User> saveAll(List<User> user) {
        List<UserEntity> userEntities = user.stream().map(UserEntity::create).toList();
        return jpaUserRepository.saveAll(userEntities)
                .stream()
                .map(UserEntity::toDomain)
                .toList();
    }

    @Override
    public User findById(Long userId) {
        return jpaUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND_EXCEPTION.getMessage()))
                .toDomain();
    }
}

package com.wirebarley.domain.user;

import java.util.List;

public interface UserRepository {

    User save(User user);

    List<User> saveAll(List<User> user);

    User findById(Long userId);
}

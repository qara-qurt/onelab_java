package org.onelab.repository;

import org.onelab.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, UserDto> users = new HashMap<>();

    public void save(UserDto user) {
        users.put(user.getId(), user);
    }

    public UserDto findById(Long id) {
        return users.get(id);
    }

    @Override
    public List<UserDto> findAll() {
        return users.values().stream().toList();
    }
}

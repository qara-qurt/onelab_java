package org.onelab.repository;

import org.onelab.entity.User;

import java.util.List;

public interface UserRepository {
    void save(User userDto);
    User findById(Long id);
    List<User> findAll();
}

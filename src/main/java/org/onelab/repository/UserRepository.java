package org.onelab.repository;

import org.onelab.dto.UserDto;

import java.util.List;

public interface UserRepository {
    void save(UserDto userDto);
    UserDto findById(Long id);
    List<UserDto> findAll();
}

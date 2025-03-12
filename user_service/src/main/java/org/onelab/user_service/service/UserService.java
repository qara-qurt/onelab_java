package org.onelab.user_service.service;

import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;

import java.util.List;

public interface UserService {
    String login(UserLoginDto userLoginDto);
    Long register(UserDto userDto);
    void withDrawBalance(String orderId, String userId, double price);
    void fillBalance(Long userId, double amount);
    UserDto getUserByID(Long id);
    List<UserDto> searchUsers(String text, int page, int size);
    List<UserDto> getUsers(int page, int size);
    String removeUser(Long id);
}

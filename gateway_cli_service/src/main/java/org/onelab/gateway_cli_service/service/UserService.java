package org.onelab.gateway_cli_service.service;

import org.onelab.gateway_cli_service.dto.Role;

import java.util.List;

public interface UserService {
    String login(String username, String password);
    String getUserByID(Long id);
    String createUser(String name, String surname, String username, String phone, String password, List<Role> roles);
    String searchUsers(String name, int page, int size);
    String getUsers(int page, int size);
    String removeUser(Long id);
    String fillBalance(Long userId, double amount);
}

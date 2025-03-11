package org.onelab.gateway_cli_service.service;

public interface UserService {
    String getUserByID(String id);
    String createUser(String name, String surname, String username,String phone,String password);
    String searchUsers(String name, int page, int size);
    String getUsers(int page, int size);
    String removeUser(String id);
    String fillBalance(String userId, double amount);
}

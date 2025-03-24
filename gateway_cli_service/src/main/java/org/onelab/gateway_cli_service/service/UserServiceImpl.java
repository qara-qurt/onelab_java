package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.client.TokenStorage;
import org.onelab.gateway_cli_service.client.UserClient;
import org.onelab.gateway_cli_service.config.ClientConfig;
import org.onelab.common_lib.enums.Role;
import org.onelab.common_lib.dto.UserDto;
import org.onelab.common_lib.dto.UserLoginDto;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserClient userClient;
    private final TokenStorage tokenStorage;
    private final KafkaProducer kafkaProducer;


    @Override
    public String login(String username, String password) {
        try {
            UserLoginDto loginRequest = UserLoginDto.builder()
                    .username(username)
                    .password(password)
                    .build();

            Map<String, String> response = userClient.loginUser(loginRequest);

            if (response == null || !response.containsKey("token")) {
                return "❌ Ошибка авторизации.";
            }

            String token = response.get("token");
            ClientConfig.setToken(token);
            tokenStorage.setToken(token);

            return "✅ Успешный вход. Токен сохранен.";
        } catch (Exception e) {
            return "❌ Ошибка при авторизации: " + e.getMessage();
        }
    }


    @Override
    public String getUserByID(Long id) {
        UserDto user = userClient.getUserById(id);
        return Utils.formatUser(user);
    }

    @Override
    public String createUser(String name, String surname, String username, String phone, String password, List<Role> roles) {
        String validationError = Utils.validateUserInput(name, surname, username, phone, password);
        if (validationError != null) return validationError;

        UserDto userDto = UserDto.builder()
                .name(name)
                .surname(surname)
                .username(username)
                .phone(phone)
                .password(password)
                .roles(roles)
                .isActive(true)
                .balance(0.0)
                .build();

        try {
            UserDto createdUser = userClient.registerUser(userDto);
            return "✅ Пользователь " + createdUser.getUsername() + " зарегистрирован. ID: " + createdUser.getId();
        } catch (Exception e) {
            return "❌ Ошибка при создании пользователя: " + e.getMessage();
        }
    }


    @Override
    public String searchUsers(String name, int page, int size) {
        List<UserDto> users = userClient.getAllUsers(page, size);
        List<UserDto> filteredUsers = users.stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();

       if (filteredUsers.isEmpty()) {
           return "❌ Пользователь с именем '" + name + "' не найден.";
       }

       return filteredUsers.stream()
               .map(Utils::formatUser)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getUsers(int page, int size) {
        List<UserDto> users = userClient.getAllUsers(page, size);
        if (users.isEmpty()) {
            return "❌ Пользователи не найдены.";
        }

        return users.stream()
                .map(Utils::formatUser)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String removeUser(Long id) {
        try {
            userClient.deleteUser(id);
            return "🗑 Пользователь с ID " + id + " удален.";
        } catch (Exception e) {
            return "❌ Ошибка при удалении пользователя: " + e.getMessage();
        }
    }

    @Override
    public String fillBalance(Long userId, double amount) {
        kafkaProducer.fillBalance(userId,amount);
        return "Запрос обрабатвается";
    }
}

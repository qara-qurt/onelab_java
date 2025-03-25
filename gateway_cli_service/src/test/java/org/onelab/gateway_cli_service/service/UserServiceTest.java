package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.common_lib.dto.UserDto;
import org.onelab.common_lib.dto.UserLoginDto;
import org.onelab.common_lib.enums.Role;
import org.onelab.gateway_cli_service.client.TokenStorage;
import org.onelab.gateway_cli_service.client.UserClient;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserClient userClient;
    private TokenStorage tokenStorage;
    private KafkaProducer kafkaProducer;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userClient = mock(UserClient.class);
        tokenStorage = mock(TokenStorage.class);
        kafkaProducer = mock(KafkaProducer.class);
        userService = new UserServiceImpl(userClient, tokenStorage, kafkaProducer);
    }

    @Test
    void testLogin_whenSuccess_returnsTokenMessage() {
        UserLoginDto expectedLogin = UserLoginDto.builder()
                .username("user")
                .password("pass")
                .build();

        when(userClient.loginUser(any())).thenReturn(Map.of("token", "jwt.token"));

        String result = userService.login("user", "pass");

        assertEquals("✅ Успешный вход. Токен сохранен.", result);
        verify(tokenStorage).setToken("jwt.token");
    }

    @Test
    void testLogin_whenNoToken_returnsError() {
        when(userClient.loginUser(any())).thenReturn(Map.of());

        String result = userService.login("user", "pass");

        assertEquals("❌ Ошибка авторизации.", result);
    }

    @Test
    void testLogin_whenExceptionThrown_returnsErrorMessage() {
        when(userClient.loginUser(any())).thenThrow(new RuntimeException("Ошибка"));

        String result = userService.login("user", "pass");

        assertEquals("❌ Ошибка при авторизации: Ошибка", result);
    }

    @Test
    void testGetUserById_returnsFormattedUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .username("tester")
                .phone("+77001112233")
                .balance(100.0)
                .isActive(true)
                .build();

        when(userClient.getUserById(1L)).thenReturn(user);

        String result = userService.getUserByID(1L);

        assertEquals(Utils.formatUser(user), result);
    }

    @Test
    void testCreateUser_whenValid_returnsSuccessMessage() {
        UserDto created = UserDto.builder()
                .id(10L)
                .username("testuser")
                .build();

        when(userClient.registerUser(any())).thenReturn(created);

        String result = userService.createUser("Name", "Surname", "testuser", "+77001112233", "password",
                List.of(Role.USER));

        assertEquals("✅ Пользователь testuser зарегистрирован. ID: 10", result);
    }

    @Test
    void testCreateUser_whenInvalid_returnsValidationError() {
        String result = userService.createUser("", "", "", "invalid", "123", List.of());

        assertTrue(result.contains("❌ Имя не может быть пустым"));
        assertTrue(result.contains("❌ Фамилия не может быть пустой"));
        assertTrue(result.contains("❌ Логин не может быть пустым"));
        assertTrue(result.contains("❌ Некорректный номер телефона"));
        assertTrue(result.contains("❌ Пароль должен быть не менее 6 символов"));
    }

    @Test
    void testCreateUser_whenException_returnsErrorMessage() {
        when(userClient.registerUser(any())).thenThrow(new RuntimeException("Ошибка регистрации"));

        String result = userService.createUser("Name", "Surname", "user", "+77001112233", "password",
                List.of(Role.USER));

        assertEquals("❌ Ошибка при создании пользователя: Ошибка регистрации", result);
    }

    @Test
    void testSearchUsers_whenFound_returnsFormatted() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .surname("Ivanov")
                .username("ivan123")
                .phone("+77001112233")
                .balance(50.0)
                .isActive(true)
                .build();

        when(userClient.getAllUsers(1, 10)).thenReturn(List.of(user));

        String result = userService.searchUsers("ivan", 1, 10);

        assertEquals(Utils.formatUser(user), result);
    }

    @Test
    void testSearchUsers_whenNotFound_returnsErrorMessage() {
        when(userClient.getAllUsers(1, 10)).thenReturn(List.of());

        String result = userService.searchUsers("notfound", 1, 10);

        assertEquals("❌ Пользователь с именем 'notfound' не найден.", result);
    }

    @Test
    void testGetUsers_whenFound_returnsFormattedList() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Anna")
                .surname("Smith")
                .username("anna_s")
                .phone("+77001112233")
                .balance(90.0)
                .isActive(true)
                .build();

        when(userClient.getAllUsers(0, 5)).thenReturn(List.of(user));

        String result = userService.getUsers(0, 5);

        assertEquals(Utils.formatUser(user), result);
    }

    @Test
    void testGetUsers_whenEmpty_returnsErrorMessage() {
        when(userClient.getAllUsers(0, 5)).thenReturn(Collections.emptyList());

        String result = userService.getUsers(0, 5);

        assertEquals("❌ Пользователи не найдены.", result);
    }

    @Test
    void testRemoveUser_whenSuccess_returnsSuccessMessage() {
        String result = userService.removeUser(10L);

        verify(userClient).deleteUser(10L);
        assertEquals("🗑 Пользователь с ID 10 удален.", result);
    }

    @Test
    void testRemoveUser_whenException_returnsErrorMessage() {
        doThrow(new RuntimeException("Удаление невозможно")).when(userClient).deleteUser(15L);

        String result = userService.removeUser(15L);

        assertEquals("❌ Ошибка при удалении пользователя: Удаление невозможно", result);
    }

    @Test
    void testFillBalance_returnsProcessingMessage_andSendsKafkaEvent() {
        String result = userService.fillBalance(5L, 100.0);

        verify(kafkaProducer).fillBalance(5L, 100.0);
        assertEquals("Запрос обрабатвается", result);
    }
}

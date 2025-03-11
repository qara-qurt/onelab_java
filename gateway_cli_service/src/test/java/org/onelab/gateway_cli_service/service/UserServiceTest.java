package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.gateway_cli_service.entity.User;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final User diasUser = User.builder()
            .id("1")
            .name("Dias")
            .surname("Serikov")
            .username("dias")
            .phone("+7777777777")
            .password("hashedPass")
            .balance(0.0)
            .isActive(true)
            .createdAt(Instant.now())
            .build();

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(diasUser));

        String result = userService.getUserByID("1");

        assertTrue(result.contains("Dias"));
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        String result = userService.getUserByID("1");

        assertEquals("❌ Пользователь с ID 1 не найден.", result);
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void createUser_shouldThrowException_whenUsernameExists() {
        when(userRepository.findByUsername("dias")).thenReturn(Optional.of(diasUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("Dias", "Serikov", "dias", "+7777777777", "password")
        );

        assertEquals("❌ Пользователь с именем dias уже существует.", exception.getMessage());
    }

    @Test
    void createUser_shouldThrowException_whenPhoneExists() {
        when(userRepository.findByUsername("dias")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("+7777777777")).thenReturn(Optional.of(diasUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("Dias", "Serikov", "dias", "+7777777777", "password")
        );

        assertEquals("❌ Пользователь с телефоном +7777777777 уже существует.", exception.getMessage());
    }

    @Test
    void createUser_shouldCreateUser_whenDataIsValid() {
        when(userRepository.findByUsername("dias")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("+7777777777")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPass");

        String result = userService.createUser("Dias", "Serikov", "dias", "+7777777777", "password");

        assertTrue(result.contains("Создание пользователя"));
        verify(kafkaProducer, times(1)).sendUser(any(User.class));
    }

    @Test
    void searchUsers_shouldReturnUsers_whenMatchFound() {
        Page<User> page = new PageImpl<>(List.of(diasUser));

        when(userRepository.searchByFields(eq("Dias"), any(PageRequest.class))).thenReturn(page);

        String result = userService.searchUsers("Dias", 1, 10);

        assertTrue(result.contains("Dias"));
        verify(userRepository, times(1)).searchByFields(eq("Dias"), any(PageRequest.class));
    }

    @Test
    void getUsers_shouldReturnUsers_whenUsersExist() {
        Page<User> page = new PageImpl<>(List.of(diasUser));

        when(userRepository.findAllUsers(any(PageRequest.class))).thenReturn(page);

        String result = userService.getUsers(1, 10);

        assertTrue(result.contains("Dias"));
        verify(userRepository, times(1)).findAllUsers(any(PageRequest.class));
    }

    @Test
    void getUsers_shouldReturnNotFoundMessage_whenNoUsersExist() {
        when(userRepository.findAllUsers(any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        String result = userService.getUsers(1, 10);

        assertEquals("❌ Пользователи не найдены.", result);
    }

    @Test
    void removeUser_shouldDeleteUser_whenUserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(diasUser));

        String result = userService.removeUser("1");

        assertEquals("🗑 Пользователь с ID 1 удален.", result);
        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void fillBalance_shouldSendKafkaMessage_whenUserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(diasUser));

        String result = userService.fillBalance("1", 100.0);

        assertEquals("✅ Баланс пользователя 1 пополнится 100.0 kz.", result);
        verify(kafkaProducer, times(1)).fillBalance("1", 100.0);
    }
}
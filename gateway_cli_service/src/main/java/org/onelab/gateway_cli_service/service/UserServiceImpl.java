package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.entity.User;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.UserRepository;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String getUserByID(String id) {
        System.out.println("🔍 Поиск пользователя с id: " + id);
        try {
            Optional<User> user = userRepository.findById(id.trim());
            return user.map(Utils::formatUser)
                    .orElse("❌ Пользователь с ID " + id + " не найден.");
        } catch (Exception e) {
            throw new IllegalArgumentException("❌ Ошибка при получении пользователя: " + e.getMessage());
        }
    }

    @Override
    public String createUser(String name, String surname, String username, String phone, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("❌ Пользователь с именем " + username + " уже существует.");
        }
        if(userRepository.findByPhone(phone).isPresent()) {
            throw new IllegalArgumentException("❌ Пользователь с телефоном " + phone + " уже существует.");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user =  User.builder()
                .name(name)
                .surname(surname)
                .username(username)
                .phone(phone)
                .password(hashedPassword)
                .isActive(true)
                .balance(0.0)
                .createdAt(Instant.now())
                .build();

        kafkaProducer.sendUser(user);
        return ("🔧 Создание пользователя: " + name + " " + surname);
    }

    @Override
    public String searchUsers(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userRepository.searchByFields(name, pageable);
        List<User> users = userPage.getContent();

        if (users.isEmpty()) {
            return "❌ Пользователь с именем '" + name + "' не найден.";
        }

        return users.stream()
                .map(Utils::formatUser)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> users = userRepository.findAllUsers(pageable);

        if (users.isEmpty()) {
            return "❌ Пользователи не найдены.";
        }

        return users.stream()
                .map(Utils::formatUser)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String removeUser(String id) {
        Optional<User> user = userRepository.findById(id.trim());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("❌ Пользователь с ID " + id + " не найден.");
        }
        userRepository.deleteById(id.trim());
        return "🗑 Пользователь с ID " + id + " удален.";
    }

    @Override
    public String fillBalance(String userId, double amount) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("❌ Пользователь с ID " + userId + " не найден.");
        }

        kafkaProducer.fillBalance(userId, amount);
        return "✅ Баланс пользователя " + userId + " пополнится " + amount + " kz.";
    }
}

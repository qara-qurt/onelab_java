package org.onelab.user_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.user_service.exception.AlreadyExistException;
import org.onelab.user_service.exception.NotFoundException;
import org.onelab.user_service.kafka.KafkaProducer;
import org.springframework.stereotype.Service;
import org.onelab.user_service.entity.User;
import org.onelab.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    public String save(User user) {
        Optional<User> existByUsername = userRepository.findByUsername(user.getUsername());
        if (existByUsername.isPresent()) {
            throw new AlreadyExistException("User with this username already exists");
        }

        Optional<User> existByPhone = userRepository.findByPhone(user.getPhone());
        if (existByPhone.isPresent()) {
            throw new AlreadyExistException("User with this phone already exists");
        }

        return userRepository.save(user).getId();
    }

    @Override
    public void withDrawBalance(String orderId, String userId, double price) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            kafkaProducer.failedPaid(orderId, userId, price);
            return;
        }

        User user = userOpt.get();

        if (user.getBalance() < price) {
            kafkaProducer.failedPaid(orderId, userId, price);
            return;
        }

        user.setBalance(user.getBalance() - price);
        userRepository.save(user);

        kafkaProducer.successPaid(orderId, userId, price);
    }

    @Override
    public void fillBalance(String userId, double amount) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found.");
        }

        User user = userOpt.get();
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }
}

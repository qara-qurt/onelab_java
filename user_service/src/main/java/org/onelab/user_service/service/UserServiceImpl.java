package org.onelab.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;
import org.onelab.user_service.entity.Role;
import org.onelab.user_service.entity.UserDocument;
import org.onelab.user_service.entity.UserEntity;
import org.onelab.user_service.exception.AlreadyExistException;
import org.onelab.user_service.exception.NotFoundException;
import org.onelab.user_service.kafka.KafkaProducer;
import org.onelab.user_service.mapper.UserMapper;
import org.onelab.user_service.repository.UserElasticRepository;
import org.onelab.user_service.repository.UserRepository;
import org.onelab.user_service.utils.JwtToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserElasticRepository userElasticRepository;
    private final KafkaProducer kafkaProducer;
    private final AuthenticationManager authenticationManager;
    private final JwtToken jwtToken;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Long register(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new AlreadyExistException("User with this username already exists");
        }
        if (userRepository.findByPhone(userDto.getPhone()).isPresent()) {
            throw new AlreadyExistException("User with this phone already exists");
        }

        userDto.setBalance(0.0);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setActive(true);
        userDto.setRoles(List.of(Role.USER));

        UserEntity userEntity = userRepository.save(UserMapper.toEntity(userDto));
        return userEntity.getId();
    }

    public String login(UserLoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();

        return jwtToken.generateToken(userDetails);
    }

    @Override
    public void withDrawBalance(String orderId, String userId, double price) {
        UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found."));

        if (user.getBalance() < price) {
            kafkaProducer.failedPaid(orderId, userId, price);
            return;
        }

        user.setBalance(user.getBalance() - price);
        userRepository.save(user);
        kafkaProducer.successPaid(orderId, userId, price);
    }

    @Override
    public void fillBalance(Long userId, double amount) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found."));
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }

    @Override
    public UserDto getUserByID(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found."));
    }

    @Override
    public List<UserDto> searchUsers(String text, int page, int size) {
        Page<UserDocument> users = userElasticRepository.searchByFields(text, PageRequest.of(page - 1, size, Sort.by("username")));
        return users.getContent().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public List<UserDto> getUsers(int page, int size) {
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(page - 1, size));
        return users.getContent().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public String removeUser(Long id) {
        Optional<UserEntity> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }

        existingUser.get().setActive(false);
        userRepository.save(existingUser.get());
        return "User successfully deleted";
    }

}

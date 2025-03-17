package org.onelab.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        UserEntity userEntity = userRepository.save(UserMapper.toEntity(userDto));
        return userEntity.getId();
    }

    public String login(UserLoginDto request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();

        return jwtToken.generateToken(userDetails, user.getId());
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

    @Override
    public Map<String, Long> compareStreamPerformance() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();

        Instant startSequential = Instant.now();
        double totalBalanceSequential = users.stream()
                .map(UserDto::getBalance)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .reduce(0.0, Double::sum);
        Instant endSequential = Instant.now();

        Instant startParallel = Instant.now();
        double totalBalanceParallel = users.parallelStream()
                .map(UserDto::getBalance)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .reduce(0.0, Double::sum);
        Instant endParallel = Instant.now();

        long sequentialTime = Duration.between(startSequential, endSequential).toMillis();
        long parallelTime = Duration.between(startParallel, endParallel).toMillis();

        log.info("Sequential Stream Time: {} ms, Total Balance: {}", sequentialTime, totalBalanceSequential);
        log.info("Parallel Stream Time: {} ms, Total Balance: {}", parallelTime, totalBalanceParallel);


        return Map.of(
                "Sequential Time (ms)", sequentialTime,
                "Total Balance Sequential", (long) totalBalanceSequential,
                "Parallel Time (ms)", parallelTime,
                "Total Balance Parallel", (long) totalBalanceParallel
        );
    }


    @Override
    public List<UserDto> filterStreamUsers(Double minBalance, Double maxBalance, int page, int size) {
        double min = (minBalance == null) ? 0.0 : minBalance;
        double max = (maxBalance == null) ? Double.MAX_VALUE : maxBalance;

        List<UserDto> users = userRepository.findAll().stream()
                .filter(user -> user.getBalance() >= min && user.getBalance() <= max)
                .sorted(Comparator.comparing(UserEntity::getBalance))
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        Optional<Double> totalSum = users.stream().map(UserDto::getBalance).reduce(Double::sum);
        totalSum.ifPresent(aDouble -> log.info("Total sum of users balance: {}", aDouble));

        return users;
    }

    @Override
    public List<UserDto> filterElasticUsers(Double minBalance, Double maxBalance, int page, int size) {
        double min = (minBalance == null) ? 0.0 : minBalance;
        double max = (maxBalance == null) ? Double.MAX_VALUE : maxBalance;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("balance").ascending());

        return userElasticRepository.findByBalanceBetween(min, max, pageable)
                .map(UserMapper::toDto).stream().collect(Collectors.toList());
    }

    @Override
    public List<UserDto> filterBirthDate(LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String sortOrder) {
        String start = (startDate == null) ? "1900-01-01" : startDate.toString();
        String end = (endDate == null) ? LocalDate.now().toString() : endDate.toString();

        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        return userElasticRepository.findByBirthDateBetween(start, end, pageable)
                    .map(UserMapper::toDto).stream().collect(Collectors.toList());
    }

}

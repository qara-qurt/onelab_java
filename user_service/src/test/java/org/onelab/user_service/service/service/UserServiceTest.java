package org.onelab.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common_lib.dto.UserDto;
import org.onelab.common_lib.dto.UserLoginDto;
import org.onelab.common_lib.enums.Role;
import org.onelab.user_service.entity.UserEntity;
import org.onelab.user_service.exception.AlreadyExistException;
import org.onelab.user_service.exception.NotFoundException;
import org.onelab.user_service.kafka.KafkaProducer;
import org.onelab.user_service.repository.UserElasticRepository;
import org.onelab.user_service.repository.UserRepository;
import org.onelab.user_service.service.UserServiceImpl;
import org.onelab.user_service.utils.JwtToken;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserElasticRepository userElasticRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtToken jwtToken;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity mockUser;
    private UserDto mockUserDto;
    private UserLoginDto mockLoginDto;

    private UserEntity user1, user2, user3;

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .password("encodedpassword")
                .balance(100.0)
                .roles(List.of(Role.USER))
                .isActive(true)
                .build();

        mockUserDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .balance(100.0)
                .build();

        user1 = UserEntity.builder()
                .id(1L)
                .username("user1")
                .balance(100.0)
                .birthDate(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .build();

        user2 = UserEntity.builder()
                .id(2L)
                .username("user2")
                .balance(200.0)
                .birthDate(LocalDate.of(1995, 6, 15))
                .isActive(true)
                .build();

        user3 = UserEntity.builder()
                .id(3L)
                .username("user3")
                .balance(50.0)
                .birthDate(LocalDate.of(2000, 12, 30))
                .isActive(true)
                .build();

        mockLoginDto = new UserLoginDto("testuser", "password");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        Long userId = userService.register(mockUserDto);

        assertNotNull(userId);
        assertEquals(1L, userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        assertThrows(AlreadyExistException.class, () -> userService.register(mockUserDto));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtToken.generateToken(any(), anyLong())).thenReturn("mockToken");

        String token = userService.login(mockLoginDto);

        assertNotNull(token);
        assertEquals("mockToken", token);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.login(mockLoginDto));
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(mockLoginDto));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        UserDto userDto = userService.getUserByID(1L);

        assertNotNull(userDto);
        assertEquals("testuser", userDto.getUsername());

        verify(userRepository, times(1)).findById(anyLong());
    }


    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByID(1L));
    }

    @Test
    void testFillBalance_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        userService.fillBalance(1L, 50.0);

        assertEquals(150.0, mockUser.getBalance());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testFillBalance_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.fillBalance(1L, 50.0));
    }

    @Test
    void testWithDrawBalance_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        userService.withDrawBalance("order123", "1", 50.0);

        assertEquals(50.0, mockUser.getBalance());
        verify(kafkaProducer, times(1)).successPaid("order123", "1", 50.0);
    }

    @Test
    void testWithDrawBalance_InsufficientFunds() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        userService.withDrawBalance("order123", "1", 200.0);

        assertEquals(100.0, mockUser.getBalance()); // Balance should remain unchanged
        verify(kafkaProducer, times(1)).failedPaid("order123", "1", 200.0);
    }

    @Test
    void testWithDrawBalance_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.withDrawBalance("order123", "1", 50.0));
    }

    @Test
    void testFilterStreamUsersByBalance() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDto> filteredUsers = userService.filterStreamUsers(60.0, 150.0, 1, 10);

        assertEquals(1, filteredUsers.size());
        assertEquals(100.0, filteredUsers.get(0).getBalance());
    }

    @Test
    void testFilterStreamUsers_NoMinBalance() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDto> filteredUsers = userService.filterStreamUsers(null, 150.0, 1, 10);

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.stream().allMatch(user -> user.getBalance() <= 150.0));
    }

    @Test
    void testFilterStreamUsers_NoMaxBalance() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDto> filteredUsers = userService.filterStreamUsers(60.0, null, 1, 10);

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.stream().allMatch(user -> user.getBalance() >= 60.0));
    }


    @Test
    void testFilterUsersByBirthDateWithOptional() {
        when(userElasticRepository.findByBirthDateBetween(
                anyString(), anyString(), any(PageRequest.class)
        )).thenReturn(new PageImpl<>(List.of()));

        List<UserDto> filteredUsers = userService.filterBirthDate(
                LocalDate.of(1991, 1, 1),
                LocalDate.of(2000, 1, 1),
                1, 10, "birthDate", "asc"
        );

        assertNotNull(filteredUsers);
        assertTrue(filteredUsers.isEmpty());
    }

    @Test
    void testCalculateTotalBalance() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        double totalBalance = userService.filterStreamUsers(0.0, Double.MAX_VALUE, 1, 10)
                .stream()
                .map(UserDto::getBalance)
                .reduce(0.0, Double::sum);

        assertEquals(350.0, totalBalance);
    }

}

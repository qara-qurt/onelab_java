//package org.onelab.user_service.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.user_service.entity.UserDocument;
//import org.onelab.user_service.exception.AlreadyExistException;
//import org.onelab.user_service.exception.NotFoundException;
//import org.onelab.user_service.kafka.KafkaProducer;
//import org.onelab.user_service.repository.UserElasticRepository;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserElasticRepository userRepository;
//
//    @Mock
//    private KafkaProducer kafkaProducer;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private final UserDocument testUser = UserDocument.builder()
//            .id("1")
//            .username("dias")
//            .phone("+7777777777")
//            .balance(100.0)
//            .build();
//
//    @Test
//    void save_shouldThrowException_whenUsernameExists() {
//        when(userRepository.findByUsername("dias")).thenReturn(Optional.of(testUser));
//
//        assertThrows(AlreadyExistException.class, () -> userService.save(testUser));
//    }
//
//    @Test
//    void save_shouldThrowException_whenPhoneExists() {
//        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
//        when(userRepository.findByPhone(testUser.getPhone())).thenReturn(Optional.of(testUser));
//
//        assertThrows(AlreadyExistException.class, () -> userService.save(testUser));
//    }
//
//    @Test
//    void save_shouldReturnUserId_whenUserIsValid() {
//        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
//        when(userRepository.findByPhone(testUser.getPhone())).thenReturn(Optional.empty());
//        when(userRepository.save(any(UserDocument.class))).thenReturn(testUser);
//
//        String userId = userService.save(testUser);
//
//        assertEquals("1", userId);
//    }
//
//    @Test
//    void withDrawBalance_shouldThrowException_whenUserNotFound() {
//        when(userRepository.findById("1")).thenReturn(Optional.empty());
//
//        userService.withDrawBalance("order123", "1", 100);
//
//        verify(kafkaProducer).failedPaid("order123", "1", 100);
//    }
//
//
//    @Test
//    void withDrawBalance_shouldFail_whenInsufficientBalance() {
//        UserDocument userWithLowBalance = UserDocument.builder().id("1").balance(50.0).build();
//        when(userRepository.findById("1")).thenReturn(Optional.of(userWithLowBalance));
//
//        userService.withDrawBalance("orderId", "1", 100);
//
//        verify(kafkaProducer).failedPaid("orderId", "1", 100);
//    }
//
//    @Test
//    void withDrawBalance_shouldSuccess_whenEnoughBalance() {
//        UserDocument user = UserDocument.builder().id("1").balance(200.0).build();
//
//        when(userRepository.findById("1")).thenReturn(Optional.of(user));
//        when(userRepository.save(any(UserDocument.class))).thenReturn(user);
//
//        userService.withDrawBalance("order123", "1", 100);
//
//        verify(kafkaProducer).successPaid("order123", "1", 100);
//        assertEquals(100, user.getBalance());
//    }
//
//
//    @Test
//    void fillBalance_shouldThrowException_whenUserNotFound() {
//        when(userRepository.findById("1")).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> userService.fillBalance("1", 100));
//    }
//
//    @Test
//    void fillBalance_shouldSucceed_whenUserExists() {
//        UserDocument user = UserDocument.builder().id("1").balance(50.0).build();
//
//        when(userRepository.findById("1")).thenReturn(Optional.of(user));
//
//        userService.fillBalance("1", 50);
//
//        verify(userRepository, times(1)).save(user);
//        assertEquals(100, user.getBalance());
//    }
//}
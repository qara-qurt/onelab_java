package org.onelab.user_service.service;

import org.onelab.user_service.dto.OrderDto;
import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.dto.UserLoginDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserService {
    String login(UserLoginDto userLoginDto);
    Long register(UserDto userDto);
    void withDrawBalance(OrderDto orderDto,String businessKey);
    void fillBalance(Long userId, double amount);
    UserDto getUserByID(Long id);
    List<UserDto> searchUsers(String text, int page, int size);
    List<UserDto> filterStreamUsers(Double minBalance,Double maxBalance, int page, int size);
    List<UserDto> filterElasticUsers(Double minBalance,Double maxBalance, int page, int size);
    List<UserDto> filterBirthDate(LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String order);
    List<UserDto> getUsers(int page, int size);
    String removeUser(Long id);
    Map<String, Long> compareStreamPerformance();
}

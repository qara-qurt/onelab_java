package org.onelab.service;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;

import java.util.List;

public interface RestaurantService {
    void addUser(UserDto userDto);
    UserDto getUser(Long id);
    List<UserDto> getUsers();
    void addOrder(OrderDto orderDto);
    void addDish(DishDto dishDto);
    List<DishDto> getDishes();
    List<OrderDto> getOrders();
}

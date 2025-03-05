package org.onelab.service;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.User;

import java.util.List;

public interface RestaurantService {
    Long addUser(UserDto userDto);
    UserDto getUser(Long id);
    List<UserDto> getUsers();
    Long addOrder(OrderDto orderDto);
    Long addDish(DishDto dishDto);
    List<DishDto> getDishes();
    List<OrderDto> getOrders();
    OrderDto getOrder(long orderId);
    OrderDto updateOrder(OrderDto order);
}

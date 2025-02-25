package org.onelab.service;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService{

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public RestaurantServiceImpl(UserRepository userRepository, DishRepository dishRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void addUser(UserDto userDto) {
        userRepository.save(userDto);
    }

    @Override
    public UserDto getUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void addOrder(OrderDto orderDto) {
        orderRepository.save(orderDto);
    }


    @Override
    public void addDish(DishDto dishDto) {
        dishRepository.save(dishDto);
    }

    @Override
    public List<DishDto> getDishes() {
        return dishRepository.findAll();
    }

    @Override
    public List<OrderDto> getOrders() {
        return orderRepository.findAll();
    }
}

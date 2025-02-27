package org.onelab.service;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;

import org.onelab.entity.User;
import org.onelab.mapper.EntityDtoMapper;
import org.onelab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

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
        userRepository.save(EntityDtoMapper.toUser(userDto));
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id);
        return user != null ? EntityDtoMapper.toUserDto(user) : null;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(EntityDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addOrder(OrderDto orderDto) {
        double totalPrice = orderDto.getDishes().stream()
                .mapToDouble(DishDto::getPrice)
                .sum();

        orderDto.setTotalPrice(totalPrice);
        orderRepository.save(EntityDtoMapper.toOrder(orderDto));
    }

    @Override
    public void addDish(DishDto dishDto) {
        dishRepository.save(EntityDtoMapper.toDish(dishDto));
    }

    @Override
    public List<DishDto> getDishes() {
        return dishRepository.findAll().stream()
                .map(EntityDtoMapper::toDishDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream()
                .map(EntityDtoMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrder(long orderId) {
        return EntityDtoMapper.toOrderDto(orderRepository.findById(orderId));
    }

    @Override
    public void updateOrder(OrderDto order) {
        orderRepository.save(EntityDtoMapper.toOrder(order));
    }
}

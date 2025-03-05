package org.onelab.service;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.Dish;
import org.onelab.entity.Order;
import org.onelab.entity.User;
import org.onelab.mapper.EntityDtoMapper;
import org.onelab.repository.DishRepository;
import org.onelab.repository.OrderRepository;
import org.onelab.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;

    public RestaurantServiceImpl(UserRepository userRepository, DishRepository dishRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void addUser(UserDto userDto) {
        User user = EntityDtoMapper.toUser(userDto);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(EntityDtoMapper::toUserDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(EntityDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addOrder(OrderDto orderDto) {
        double totalPrice = orderDto.getDishes().stream()
                .mapToDouble(DishDto::getPrice)
                .sum();
        orderDto.setTotalPrice(totalPrice);

        Order order = EntityDtoMapper.toOrder(orderDto);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void addDish(DishDto dishDto) {
        Dish dish = EntityDtoMapper.toDish(dishDto);
        dishRepository.save(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getDishes() {
        return dishRepository.findAll().stream()
                .map(EntityDtoMapper::toDishDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream()
                .map(EntityDtoMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(long orderId) {
        return orderRepository.findById(orderId)
                .map(EntityDtoMapper::toOrderDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateOrder(OrderDto orderDto) {
        orderRepository.findById(orderDto.getId()).ifPresent(existingOrder -> {
            existingOrder.setDishes(orderDto.getDishes().stream()
                    .map(EntityDtoMapper::toDish)
                    .collect(Collectors.toList()));
            existingOrder.setTotalPrice(orderDto.getTotalPrice());
            existingOrder.setStatus(orderDto.getStatus());
            orderRepository.save(existingOrder);
        });
    }
}

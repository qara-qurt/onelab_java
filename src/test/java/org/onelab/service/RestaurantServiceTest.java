package org.onelab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.Dish;
import org.onelab.entity.Order;
import org.onelab.entity.OrderStatus;
import org.onelab.entity.User;
import org.onelab.mapper.EntityDtoMapper;
import org.onelab.repository.DishRepository;
import org.onelab.repository.OrderRepository;
import org.onelab.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DishRepository dishRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private User user;
    private UserDto userDto;
    private Dish dish;
    private DishDto dishDto;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .phone(Long.valueOf("1234567890"))
                .build();

        user = EntityDtoMapper.toUser(userDto);

        dishDto = DishDto.builder()
                .id(1L)
                .name("Pizza")
                .price(10.0)
                .build();

        dish = EntityDtoMapper.toDish(dishDto);

        orderDto = OrderDto.builder()
                .id(1L)
                .customer(userDto)
                .dishes(List.of(dishDto))
                .totalPrice(10.0)
                .status(OrderStatus.NEW)
                .build();

        order = EntityDtoMapper.toOrder(orderDto);
    }

    @Test
    void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        Long userId = restaurantService.addUser(userDto);
        assertEquals(user.getId(), userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto foundUser = restaurantService.getUser(1L);
        assertNotNull(foundUser);
        assertEquals(userDto.getId(), foundUser.getId());
    }

    @Test
    void testGetUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = restaurantService.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userDto.getId(), users.get(0).getId());
    }

    @Test
    void testAddDish() {
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);
        Long dishId = restaurantService.addDish(dishDto);
        assertEquals(dish.getId(), dishId);
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    void testGetDishes() {
        when(dishRepository.findAll()).thenReturn(List.of(dish));
        List<DishDto> dishes = restaurantService.getDishes();
        assertNotNull(dishes);
        assertEquals(1, dishes.size());
        assertEquals(dishDto.getId(), dishes.get(0).getId());
    }

    @Test
    void testAddOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        Long orderId = restaurantService.addOrder(orderDto);
        assertEquals(order.getId(), orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testGetOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderDto foundOrder = restaurantService.getOrder(1L);
        assertNotNull(foundOrder);
        assertEquals(orderDto.getId(), foundOrder.getId());
    }

    @Test
    void testGetOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        List<OrderDto> orders = restaurantService.getOrders();
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(orderDto.getId(), orders.get(0).getId());
    }


    @Test
    void testUpdateOrder() {
        when(orderRepository.findById(orderDto.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto updatedOrder = restaurantService.updateOrder(orderDto);
        assertNotNull(updatedOrder);
        assertEquals(orderDto.getId(), updatedOrder.getId());
    }
}

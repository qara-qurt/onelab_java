package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Menu;
import org.onelab.gateway_cli_service.entity.Order;
import org.onelab.gateway_cli_service.entity.User;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.DishRepository;
import org.onelab.gateway_cli_service.repository.MenuRepository;
import org.onelab.gateway_cli_service.repository.OrderRepository;
import org.onelab.gateway_cli_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Dish testDish;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-1")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .phone("123456789")
                .balance(100.0)
                .build();

        testDish = Dish.builder()
                .id("dish-1")
                .name("Pizza")
                .description("Cheese Pizza")
                .price(10.99)
                .build();

        testOrder = Order.builder()
                .id("order-1")
                .customerId("user-1")
                .dishes(List.of(testDish))
                .totalPrice(10.99)
                .build();
    }


    @Test
    void shouldCreateOrder_WhenUserAndDishesExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(dishRepository.findAllById(List.of("dish-1"))).thenReturn(List.of(testDish));
        when(menuRepository.findAll()).thenReturn(List.of(
                Menu.builder()
                        .id("menu-1")
                        .name("Test Menu")
                        .dishes(List.of(testDish))
                        .build()
        ));

        String result = orderService.createOrder("user-1", List.of("dish-1"));

        assertTrue(result.contains("Заказ для клиента user-1 отправлен в обработку"));
        verify(kafkaProducer, times(1)).createOrder(anyString(), anyList());
    }


    @Test
    void shouldReturnError_WhenUserNotFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder("user-1", List.of("dish-1"))
        );

        assertEquals("❌ Пользователь с ID user-1 не найден.", exception.getMessage());
    }

    @Test
    void shouldReturnError_WhenDishesNotFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(dishRepository.findAllById(List.of("dish-1"))).thenReturn(List.of());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder("user-1", List.of("dish-1"))
        );

        assertTrue(exception.getMessage().contains("❌ Некоторые блюда не найдены"));
    }

    @Test
    void shouldGetOrder_WhenOrderExists() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(testOrder));

        String result = orderService.getOrder("order-1");

        assertTrue(result.contains("order-1"));
    }

    @Test
    void shouldReturnError_WhenOrderNotFound() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.empty());

        String result = orderService.getOrder("order-1");

        assertEquals("❌ Заказ с ID order-1 не найден.", result);
    }

    @Test
    void shouldReturnOrdersByUser_WhenOrdersExist() {
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findByCustomerId("user-1", PageRequest.of(0, 10))).thenReturn(orderPage);

        String result = orderService.getOrdersByUser("user-1", 1, 10);

        assertTrue(result.contains("order-1"));
    }

    @Test
    void shouldReturnError_WhenNoOrdersByUser() {
        Page<Order> emptyPage = Page.empty();
        when(orderRepository.findByCustomerId("user-1", PageRequest.of(0, 10))).thenReturn(emptyPage);

        String result = orderService.getOrdersByUser("user-1", 1, 10);

        assertEquals("❌ У клиента user-1 нет заказов.", result);
    }

    @Test
    void shouldReturnAllOrders_WhenOrdersExist() {
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        when(orderRepository.findAll(PageRequest.of(0, 10))).thenReturn(orderPage);

        String result = orderService.getOrders(1, 10);

        assertTrue(result.contains("order-1"));
    }

    @Test
    void shouldReturnError_WhenNoOrdersExist() {
        Page<Order> emptyPage = Page.empty();
        when(orderRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);

        String result = orderService.getOrders(1, 10);

        assertEquals("❌ Заказы отсутствуют.", result);
    }
}

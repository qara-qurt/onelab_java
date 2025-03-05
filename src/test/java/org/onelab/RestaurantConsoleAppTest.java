package org.onelab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.OrderStatus;
import org.onelab.service.RestaurantService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantConsoleAppTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantConsoleApp app;

    private InputStream originalSystemIn;

    @BeforeEach
    void setUp() {
        originalSystemIn = System.in;
    }

    void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        app = new RestaurantConsoleApp(restaurantService);
    }

    @Test
    void testAddUser() {
        setInput("Test\n1234567890\n");
        when(restaurantService.addUser(any(UserDto.class))).thenReturn(1L);

        String result = app.addUser();
        assertEquals("Пользователь добавлен ID: 1", result);
    }

    @Test
    void testViewUsers() {
        List<UserDto> mockUsers = List.of(UserDto.builder()
                .id(1L)
                .name("Test")
                .phone(1234567890L)
                .build());
        when(restaurantService.getUsers()).thenReturn(mockUsers);

        List<UserDto> users = app.viewUsers();
        assertEquals(1, users.size());
        assertEquals("Test", users.get(0).getName());
    }

    @Test
    void testAddDish() {
        setInput("Pizza\n1000\n");
        when(restaurantService.addDish(any(DishDto.class))).thenReturn(1L);

        String result = app.addDish();
        assertEquals("Блюдо добавлено ID: 1 - Pizza - 1000.0тг", result);
    }

    @Test
    void testViewMenu() {
        List<DishDto> mockDishes = List.of(DishDto.builder()
                .id(1L)
                .name("Pizza")
                .price(1000.0)
                .build());
        when(restaurantService.getDishes()).thenReturn(mockDishes);

        List<DishDto> dishes = app.viewMenu();
        assertEquals(1, dishes.size());
        assertEquals("Pizza", dishes.get(0).getName());
    }

    @Test
    void testCreateOrder() {
        setInput("1\n1\n0\n");

        UserDto mockUser = UserDto.builder()
                .id(1L)
                .name("Test")
                .phone(1234567890L)
                .build();
        List<DishDto> mockDishes = List.of(DishDto.builder()
                .id(1L)
                .name("Pizza")
                .price(1000.0)
                .build());
        when(restaurantService.getUser(1L)).thenReturn(mockUser);
        when(restaurantService.getDishes()).thenReturn(mockDishes);
        when(restaurantService.addOrder(any(OrderDto.class))).thenReturn(1L);

        String result = app.createOrder();
        assertEquals("Заказ создан ID 1", result);
    }

    @Test
    void testCreateOrderWithInvalidUserId() {
        setInput("abc\n");

        String result = app.createOrder();
        assertEquals("Ошибка ввода", result);
    }

    @Test
    void testCreateOrderWithNonExistentUser() {
        setInput("999\n1\n0\n");
        lenient().when(restaurantService.getUser(999L)).thenReturn(null);

        String result = app.createOrder();
        assertEquals("Пользователь не найден.", result);
    }


    @Test
    void testUpdateOrderStatus() {
        setInput("1\n1\n");

        UserDto mockUser = UserDto.builder()
                .id(1L)
                .name("Test")
                .phone(1234567890L)
                .build();
        List<DishDto> mockDishes = List.of(DishDto.builder()
                .id(1L)
                .name("Pizza")
                .price(1000.0)
                .build());
        OrderDto mockOrder = OrderDto.builder()
                .id(1L)
                .customer(mockUser)
                .dishes(mockDishes)
                .status(OrderStatus.NEW)
                .totalPrice(1000.0)
                .build();

        when(restaurantService.getOrder(1L)).thenReturn(mockOrder);
        when(restaurantService.updateOrder(any(OrderDto.class))).thenReturn(mockOrder);

        String result = app.updateOrderStatus();
        assertEquals("Статус заказа обновлён на NEW.", result);
    }

    @Test
    void testUpdateOrderStatusWithInvalidOrderId() {
        setInput("999\n");
        when(restaurantService.getOrder(999L)).thenReturn(null);

        String result = app.updateOrderStatus();
        assertEquals("Заказ не найден.", result);
    }

    @Test
    void testViewOrders() {
        UserDto mockUser = UserDto.builder()
                .id(1L)
                .name("Test")
                .phone(1234567890L)
                .build();
        List<DishDto> mockDishes = List.of(DishDto.builder()
                .id(1L)
                .name("Pizza")
                .price(1000.0)
                .build());
        List<OrderDto> mockOrders = List.of(OrderDto.builder()
                .id(1L)
                .customer(mockUser)
                .dishes(mockDishes)
                .status(OrderStatus.NEW)
                .totalPrice(1000.0)
                .build());
        when(restaurantService.getOrders()).thenReturn(mockOrders);

        List<OrderDto> orders = app.viewOrders();
        assertEquals(1, orders.size());
        assertEquals(OrderStatus.NEW, orders.get(0).getStatus());
    }
}
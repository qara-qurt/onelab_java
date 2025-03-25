package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.dto.OrderRequestDto;
import org.onelab.common_lib.enums.OrderStatus;
import org.onelab.gateway_cli_service.client.CamundaClient;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.client.TokenStorage;
import org.onelab.gateway_cli_service.utils.JwtToken;
import org.onelab.gateway_cli_service.utils.Utils;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private RestaurantClient restaurantClient;
    private CamundaClient camundaClient;
    private TokenStorage tokenStorage;
    private JwtToken jwtTokenUtil;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        restaurantClient = mock(RestaurantClient.class);
        camundaClient = mock(CamundaClient.class);
        tokenStorage = mock(TokenStorage.class);
        jwtTokenUtil = mock(JwtToken.class);
        orderService = new OrderServiceImpl(restaurantClient, camundaClient, tokenStorage, jwtTokenUtil);
    }

    @Test
    void testCreateOrder_whenAllDishesExist_returnsSuccess() {
        List<Long> dishIds = List.of(1L, 2L);
        List<DishDto> availableDishes = List.of(
                DishDto.builder().id(1L).name("Soup").build(),
                DishDto.builder().id(2L).name("Pizza").build()
        );

        when(restaurantClient.getDishes(1, 100)).thenReturn(availableDishes);
        when(camundaClient.createOrder(any(OrderRequestDto.class))).thenReturn("✅ Заказ #123");

        String result = orderService.createOrder(99L, dishIds);

        assertEquals("✅ Заказ #123 для клиента 99 создан.", result);
    }

    @Test
    void testCreateOrder_whenSomeDishesMissing_returnsError() {
        List<Long> dishIds = List.of(1L, 2L);
        List<DishDto> availableDishes = List.of(DishDto.builder().id(1L).name("Soup").build());

        when(restaurantClient.getDishes(1, 100)).thenReturn(availableDishes);

        String result = orderService.createOrder(99L, dishIds);

        assertEquals("❌ Некоторые блюда не найдены: [2]", result);
    }

    @Test
    void testCreateOrder_whenExceptionThrown_returnsErrorMessage() {
        when(restaurantClient.getDishes(1, 100)).thenThrow(new RuntimeException("Connection error"));

        String result = orderService.createOrder(1L, List.of(1L));

        assertEquals("❌ Ошибка при создании заказа: Connection error", result);
    }

    @Test
    void testGetOrder_whenOrderExists_returnsFormattedOrder() {
        OrderDto order = OrderDto.builder()
                .id(1L)
                .customerId(99L)
                .totalPrice(100.0)
                .dishes(List.of(DishDto.builder().id(1L).name("Burger").build()))
                .status(OrderStatus.NEW)
                .build();

        when(restaurantClient.getOrder(1L)).thenReturn(order);

        String expected = Utils.formatOrder(order);
        String result = orderService.getOrder(1L);

        assertEquals(expected, result);
    }

    @Test
    void testGetOrder_whenNotFound_returnsErrorMessage() {
        when(restaurantClient.getOrder(123L)).thenThrow(new RuntimeException("Not found"));

        String result = orderService.getOrder(123L);

        assertEquals("❌ Заказ с ID 123 не найден.", result);
    }

    @Test
    void testGetOrdersByUser_whenTokenMissing_returnsError() {
        when(tokenStorage.getToken()).thenReturn(null);

        String result = orderService.getOrdersByUser(1, 10);

        assertEquals("❌ Error: No authentication token found.", result);
    }

    @Test
    void testGetOrdersByUser_whenUserIdMissing_returnsError() {
        when(tokenStorage.getToken()).thenReturn("valid.token");
        when(jwtTokenUtil.getUserIdFromToken("valid.token")).thenReturn(null);

        String result = orderService.getOrdersByUser(1, 10);

        assertEquals("❌ Error: Could not extract userId from token.", result);
    }

    @Test
    void testGetOrdersByUser_whenNoOrders_returnsMessage() {
        when(tokenStorage.getToken()).thenReturn("valid.token");
        when(jwtTokenUtil.getUserIdFromToken("valid.token")).thenReturn(77L);
        when(restaurantClient.getOrdersByUser(77L, 1, 10)).thenReturn(Collections.emptyList());

        String result = orderService.getOrdersByUser(1, 10);

        assertEquals("❌ No orders found for user ID: 77", result);
    }

    @Test
    void testGetOrdersByUser_whenOrdersExist_returnsFormattedOrders() {
        OrderDto order = OrderDto.builder()
                .id(1L)
                .customerId(88L)
                .dishes(List.of(DishDto.builder().id(1L).name("Soup").build()))
                .totalPrice(30.0)
                .status(OrderStatus.NEW)
                .build();

        when(tokenStorage.getToken()).thenReturn("valid.token");
        when(jwtTokenUtil.getUserIdFromToken("valid.token")).thenReturn(88L);
        when(restaurantClient.getOrdersByUser(88L, 1, 10)).thenReturn(List.of(order));

        String result = orderService.getOrdersByUser(1, 10);

        assertEquals(Utils.formatOrder(order), result);
    }

    @Test
    void testGetOrders_whenOrdersExist_returnsFormatted() {
        OrderDto order = OrderDto.builder()
                .id(10L)
                .customerId(100L)
                .dishes(List.of(DishDto.builder().id(1L).name("Sushi").build()))
                .totalPrice(50.0)
                .status(OrderStatus.NEW)
                .build();

        when(restaurantClient.getOrders(1, 10)).thenReturn(List.of(order));

        String result = orderService.getOrders(1, 10);

        assertEquals(Utils.formatOrder(order), result);
    }

    @Test
    void testGetOrders_whenEmpty_returnsEmptyMessage() {
        when(restaurantClient.getOrders(1, 10)).thenReturn(Collections.emptyList());

        String result = orderService.getOrders(1, 10);

        assertEquals("❌ Заказы отсутствуют.", result);
    }

    @Test
    void testGetOrders_whenExceptionThrown_returnsErrorMessage() {
        when(restaurantClient.getOrders(1, 10)).thenThrow(new RuntimeException("Ошибка соединения"));

        String result = orderService.getOrders(1, 10);

        assertEquals("❌ Ошибка при получении заказов.", result);
    }
}

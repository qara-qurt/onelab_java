package org.onelab.order_worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.dto.OrderRequestDto;
import org.onelab.common_lib.enums.OrderStatus;
import org.onelab.order_worker.client.RestaurantClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private RestaurantClient restaurantClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        restaurantClient = mock(RestaurantClient.class);
        orderService = new OrderServiceImpl(restaurantClient);
    }

    @Test
    void testCreateOrder_callsRestaurantClientWithCorrectRequest() {
        Long userId = 1L;
        List<Long> dishIds = List.of(10L, 20L);

        OrderDto mockResponse = OrderDto.builder()
                .id(123L)
                .customerId(userId)
                .dishes(List.of())
                .totalPrice(150.0)
                .status(OrderStatus.NEW)
                .build();

        when(restaurantClient.createOrder(any(OrderRequestDto.class))).thenReturn(mockResponse);

        OrderDto result = orderService.createOrder(userId, dishIds);

        assertEquals(mockResponse, result);

        ArgumentCaptor<OrderRequestDto> captor = ArgumentCaptor.forClass(OrderRequestDto.class);
        verify(restaurantClient).createOrder(captor.capture());

        OrderRequestDto capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getCustomerId());
        assertEquals(dishIds, capturedRequest.getDishIds());
    }
}

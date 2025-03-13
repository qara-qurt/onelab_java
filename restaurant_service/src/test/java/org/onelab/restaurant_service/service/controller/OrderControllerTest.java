package org.onelab.restaurant_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.dto.OrderDto;
import org.onelab.restaurant_service.dto.OrderRequestDto;
import org.onelab.restaurant_service.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private OrderDto mockOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        mockOrder = OrderDto.builder()
                .id(1L)
                .customerId(100L)
                .dishes(List.of())
                .totalPrice(2500.0)
                .build();
    }

    @Test
    void createOrder_ShouldReturnOrder() throws Exception {
        when(orderService.createOrder(anyLong(), anyList())).thenReturn(mockOrder);

        String requestBody = """
        {
          "customerId": 100,
          "dishIds": [1, 2]
        }
        """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100));

        verify(orderService, times(1)).createOrder(anyLong(), anyList());
    }

    @Test
    void getOrder_ShouldReturnOrderById() throws Exception {
        when(orderService.getOrder(anyLong())).thenReturn(mockOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(100));

        verify(orderService, times(1)).getOrder(1L);
    }

    @Test
    void getOrdersByUser_ShouldReturnUserOrders() throws Exception {
        when(orderService.getOrdersByUser(anyLong(), anyInt(), anyInt())).thenReturn(List.of(mockOrder));

        mockMvc.perform(get("/api/orders/user/100?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerId").value(100));

        verify(orderService, times(1)).getOrdersByUser(100L, 1, 10);
    }

    @Test
    void getOrders_ShouldReturnAllOrders() throws Exception {
        when(orderService.getOrders(anyInt(), anyInt())).thenReturn(List.of(mockOrder));

        mockMvc.perform(get("/api/orders?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerId").value(100));

        verify(orderService, times(1)).getOrders(1, 10);
    }
}

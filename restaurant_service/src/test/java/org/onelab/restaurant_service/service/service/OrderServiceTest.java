package org.onelab.restaurant_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.enums.OrderStatus;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.entity.OrderEntity;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.OrderElasticRepository;
import org.onelab.restaurant_service.repository.OrderRepository;
import org.onelab.restaurant_service.service.OrderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderElasticRepository orderElasticRepository;

    private OrderEntity mockOrder;
    private DishEntity dish1;
    private DishEntity dish2;

    @BeforeEach
    void setUp() {
        dish1 = DishEntity.builder().id(1L).name("Pasta").price(1200.0).build();
        dish2 = DishEntity.builder().id(2L).name("Pizza").price(1500.0).build();

        mockOrder = OrderEntity.builder()
                .id(1L)
                .customerId(123L)
                .dishes(List.of(dish1, dish2))
                .totalPrice(2700.0)
                .status(OrderStatus.NEW)
                .build();
    }

    @Test
    void createOrder_ShouldReturnOrderDto_WhenAllDishesExist() {
        List<Long> dishIds = List.of(1L, 2L);

        when(dishRepository.findAllById(dishIds)).thenReturn(List.of(dish1, dish2));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(mockOrder);

        OrderDto result = orderService.createOrder(123L, dishIds);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(123L);
        assertThat(result.getTotalPrice()).isEqualTo(2700.0);
        assertThat(result.getDishes()).hasSize(2);

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderElasticRepository).save(any()); // ✅ Ensure Elasticsearch sync
    }

    @Test
    void createOrder_ShouldThrowNotFoundException_WhenDishesNotFound() {
        List<Long> dishIds = List.of(1L, 2L);

        when(dishRepository.findAllById(dishIds)).thenReturn(List.of(dish1));

        assertThatThrownBy(() -> orderService.createOrder(123L, dishIds))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Some dishes not found.");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrder_ShouldReturnOrderDto_WhenOrderExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        OrderDto result = orderService.getOrder(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(123L);
        assertThat(result.getTotalPrice()).isEqualTo(2700.0);

        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrder_ShouldThrowNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Order not found.");

        verify(orderRepository).findById(999L);
    }


}

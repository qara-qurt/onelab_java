package org.onelab.restaurant_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.entity.Menu;
import org.onelab.restaurant_service.entity.Order;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.MenuRepository;
import org.onelab.restaurant_service.repository.OrderRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_WhenAllDishesExistAndInMenu_ShouldReturnOrderId() {
        Dish dish1 = Dish.builder().id("dish1").price(1000.0).build();
        Dish dish2 = Dish.builder().id("dish2").price(500.0).build();

        Menu menu = Menu.builder().id("menu123").dishes(List.of(dish1, dish2)).build();

        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1, dish2));
        when(menuRepository.findAll()).thenReturn(List.of(menu));
        when(orderRepository.save(any())).thenReturn(Order.builder().id("order123").build());

        String orderId = orderService.createOrder("user1", List.of("dish1", "dish2"));

        assertThat(orderId).isEqualTo("order123");
        verify(orderRepository).save(any());
    }

    @Test
    void createOrder_WhenDishNotFound_ShouldThrowNotFoundException() {
        Dish dish1 = Dish.builder().id("dish1").build();

        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1));

        assertThatThrownBy(() -> orderService.createOrder("user123", List.of("dish1", "dish2")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Dishes not found: [dish2]");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WhenDishesNotInAnyMenu_ShouldThrowNotFoundException() {
        Dish dish1 = Dish.builder().id("dish1").build();
        Dish dish2 = Dish.builder().id("dish2").build();

        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1, dish2));
        when(menuRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrder("user123", List.of("dish1", "dish2")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Chosen dishes are not in any menu.");

        verify(orderRepository, never()).save(any());
    }
}
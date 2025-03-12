//package org.onelab.restaurant_service.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.restaurant_service.entity.DishDocument;
//import org.onelab.restaurant_service.entity.MenuDocument;
//import org.onelab.restaurant_service.entity.Order;
//import org.onelab.restaurant_service.exception.NotFoundException;
//import org.onelab.restaurant_service.repository.DishElasticRepository;
//import org.onelab.restaurant_service.repository.MenuElasticRepository;
//import org.onelab.restaurant_service.repository.OrderElasticRepository;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private DishElasticRepository dishRepository;
//
//    @Mock
//    private MenuElasticRepository menuRepository;
//
//    @Mock
//    private OrderElasticRepository orderRepository;
//
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    @Test
//    void createOrder_WhenAllDishesExistAndInMenu_ShouldReturnOrderId() {
//        DishDocument dish1 = DishDocument.builder().id("dish1").price(1000.0).build();
//        DishDocument dish2 = DishDocument.builder().id("dish2").price(500.0).build();
//
//        MenuDocument menu = MenuDocument.builder().id("menu123").dishes(List.of(dish1, dish2)).build();
//
//        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1, dish2));
//        when(menuRepository.findAll()).thenReturn(List.of(menu));
//        when(orderRepository.save(any())).thenReturn(Order.builder().id("order123").build());
//
//        String orderId = orderService.createOrder("user1", List.of("dish1", "dish2"));
//
//        assertThat(orderId).isEqualTo("order123");
//        verify(orderRepository).save(any());
//    }
//
//    @Test
//    void createOrder_WhenDishNotFound_ShouldThrowNotFoundException() {
//        DishDocument dish1 = DishDocument.builder().id("dish1").build();
//
//        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1));
//
//        assertThatThrownBy(() -> orderService.createOrder("user123", List.of("dish1", "dish2")))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("Dishes not found: [dish2]");
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void createOrder_WhenDishesNotInAnyMenu_ShouldThrowNotFoundException() {
//        DishDocument dish1 = DishDocument.builder().id("dish1").build();
//        DishDocument dish2 = DishDocument.builder().id("dish2").build();
//
//        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1, dish2));
//        when(menuRepository.findAll()).thenReturn(List.of());
//
//        assertThatThrownBy(() -> orderService.createOrder("user123", List.of("dish1", "dish2")))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("Chosen dishes are not in any menu.");
//
//        verify(orderRepository, never()).save(any());
//    }
//}
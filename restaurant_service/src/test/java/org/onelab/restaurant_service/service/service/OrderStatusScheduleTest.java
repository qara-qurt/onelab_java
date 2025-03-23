//package org.onelab.restaurant_service.service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.restaurant_service.entity.OrderEntity;
//import org.onelab.restaurant_service.entity.OrderStatus;
//import org.onelab.restaurant_service.kafka.KafkaProducer;
//import org.onelab.restaurant_service.repository.OrderRepository;
//import org.onelab.restaurant_service.service.OrderStatusSchedule;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderStatusScheduleTest {
//
//    @Mock
//    private KafkaProducer kafkaProducer;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @InjectMocks
//    private OrderStatusSchedule orderStatusSchedule;
//
//    private OrderEntity newOrder1;
//    private OrderEntity newOrder2;
//    private OrderEntity processingOrder;
//
//    @BeforeEach
//    void setUp() {
//        newOrder1 = OrderEntity.builder().id(1L).status(OrderStatus.NEW).build();
//        newOrder2 = OrderEntity.builder().id(2L).status(OrderStatus.NEW).build();
//        processingOrder = OrderEntity.builder()
//                .id(3L)
//                .customerId(100L)
//                .status(OrderStatus.PROCESSING)
//                .totalPrice(1500.0)
//                .build();
//    }
//
//    @Test
//    void updateOrderStatus_ShouldProcessAndCompleteOrders() {
//        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(List.of(newOrder1, newOrder2));
//        when(orderRepository.findByStatus(OrderStatus.PROCESSING)).thenReturn(List.of(processingOrder));
//
//        orderStatusSchedule.updateOrderStatus();
//
//        verify(orderRepository, times(2)).save(argThat(order -> order.getStatus() == OrderStatus.PROCESSING));
//        verify(orderRepository, times(1)).save(argThat(order -> order.getStatus() == OrderStatus.COMPLETED));
//
//        verify(kafkaProducer).withdrawOrder(
//                processingOrder.getId(),
//                processingOrder.getCustomerId(),
//                processingOrder.getTotalPrice()
//        );
//    }
//
//    @Test
//    void updateOrderStatus_WhenNoOrders_ShouldDoNothing() {
//        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(List.of());
//        when(orderRepository.findByStatus(OrderStatus.PROCESSING)).thenReturn(List.of());
//
//        orderStatusSchedule.updateOrderStatus();
//
//        verify(orderRepository, never()).save(any());
//        verify(kafkaProducer, never()).withdrawOrder(anyLong(), anyLong(), anyDouble());
//    }
//}

//package org.onelab.restaurant_service.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.restaurant_service.entity.Order;
//import org.onelab.restaurant_service.entity.OrderStatus;
//import org.onelab.restaurant_service.kafka.KafkaProducer;
//import org.onelab.restaurant_service.repository.OrderElasticRepository;
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
//    private OrderElasticRepository orderRepository;
//
//    @Test
//    void updateOrderStatus_ShouldProcessAndCompleteOrders() {
//        Order newOrder1 = Order.builder().id("order1").status(OrderStatus.NEW).build();
//        Order newOrder2 = Order.builder().id("order2").status(OrderStatus.NEW).build();
//        Order processingOrder = Order.builder()
//                .id("order456")
//                .customerId("user456")
//                .status(OrderStatus.PROCESSING)
//                .totalPrice(1500.0)
//                .build();
//
//        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(List.of(newOrder1, newOrder2));
//        when(orderRepository.findByStatus(OrderStatus.PROCESSING)).thenReturn(List.of(processingOrder));
//
//        OrderStatusSchedule schedule = new OrderStatusSchedule(kafkaProducer, orderRepository);
//
//        schedule.updateOrderStatus();
//
//        verify(orderRepository, times(2)).save(argThat(order -> order.getStatus() == OrderStatus.PROCESSING));
//        verify(orderRepository).save(argThat(order -> order.getStatus() == OrderStatus.COMPLETED));
//
//        verify(kafkaProducer).withdrawOrder(
//                processingOrder.getId(),
//                processingOrder.getCustomerId(),
//                processingOrder.getTotalPrice()
//        );
//    }
//
//
//    @Test
//    void updateOrderStatus_WhenNoOrders_ShouldDoNothing() {
//        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(List.of());
//        when(orderRepository.findByStatus(OrderStatus.PROCESSING)).thenReturn(List.of());
//
//        new OrderStatusSchedule(kafkaProducer, orderRepository).updateOrderStatus();
//
//        verify(orderRepository, never()).save(any());
//        verify(kafkaProducer, never()).withdrawOrder(anyString(), anyString(), anyDouble());
//    }
//}

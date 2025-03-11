package org.onelab.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.restaurant_service.entity.Order;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.onelab.restaurant_service.kafka.KafkaProducer;
import org.onelab.restaurant_service.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderStatusSchedule {

    private final KafkaProducer kafkaProducer;
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 60000) // 1 min
    public void updateOrderStatus() {

        // Update NEW -> PROCESSING
        List<Order> newOrders = orderRepository.findByStatus(OrderStatus.NEW);
        if (!newOrders.isEmpty()) {
            newOrders.forEach(order -> {
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
            });
        }

        // Update PROCESSING -> COMPLETED
        List<Order> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);
        if (!processingOrders.isEmpty()) {
            processingOrders.forEach(order -> {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                kafkaProducer.withdrawOrder(order.getId(), order.getCustomerId(), order.getTotalPrice());
            });
        }
    }
}

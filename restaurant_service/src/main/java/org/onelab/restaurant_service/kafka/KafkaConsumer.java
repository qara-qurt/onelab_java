package org.onelab.restaurant_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.restaurant_service.dto.OrderDto;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.onelab.restaurant_service.service.OrderService;
import org.onelab.restaurant_service.utils.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.FAILED_PAID,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFailedPayment(ConsumerRecord<String, String> record) {
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);
            order.setStatus(OrderStatus.CANCELLED);
            orderService.updateOrder(order);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.SUCCESS_PAID,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSuccessfulPayment(ConsumerRecord<String, String> record) {
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);
            order.setStatus(OrderStatus.PAID);
            orderService.updateOrder(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

}

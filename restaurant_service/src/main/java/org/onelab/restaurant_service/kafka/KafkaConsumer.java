package org.onelab.restaurant_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.enums.OrderStatus;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.onelab.restaurant_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopics.FAILED_PAID,
            groupId = KafkaTopics.RESTAURANT_GROUP_ID,
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
            groupId = KafkaTopics.RESTAURANT_GROUP_ID,
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

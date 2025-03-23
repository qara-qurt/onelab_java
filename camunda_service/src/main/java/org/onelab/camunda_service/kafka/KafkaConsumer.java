package org.onelab.camunda_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.service.UserService;
import org.onelab.camunda_service.utils.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.FAILED_PAID,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFailedPayment(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);
            userService.failedPayment(order,key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.SUCCESS_PAID,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSuccessfulPayment(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);
            userService.successPayment(order,key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

}

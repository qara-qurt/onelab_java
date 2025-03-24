package org.onelab.order_worker.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.onelab.order_worker.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.FAILED_PAID,
            groupId = KafkaTopics.ORDER_WORKER_GROUP_ID,
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
            groupId = KafkaTopics.ORDER_WORKER_GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSuccessfulPayment(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);
            userService.successPayment(order,key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

}

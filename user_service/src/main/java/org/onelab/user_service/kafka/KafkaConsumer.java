package org.onelab.user_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.user_service.service.UserService;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    // Create Order
    @KafkaListener(
            topics = KafkaTopics.WITHDRAW_ORDER,
            groupId = KafkaTopics.USER_GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void withdrawOrder(ConsumerRecord<String, String> record) {
        String key = record.key();
        String jsonPayload = record.value();

        try {
            OrderDto order = objectMapper.readValue(jsonPayload, OrderDto.class);

            userService.withDrawBalance(order,key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize OrderDto: " + e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.USER_FILL_BALANCE,
            groupId = KafkaTopics.USER_GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void fillBalance(ConsumerRecord<String, String> record) {
        String userID = record.key();
        String jsonPayload = record.value();

        try {
            String value = objectMapper.readValue(jsonPayload, String.class);
            double amount = Double.parseDouble(value);

            userService.fillBalance(Long.valueOf(userID), amount);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

}

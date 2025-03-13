package org.onelab.user_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.user_service.service.UserService;
import org.onelab.user_service.utils.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;


    // Create Order
    @KafkaListener(
            topics = KafkaTopics.WITHDRAW_ORDER,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void createOrder(ConsumerRecord<String, String> record)  {
        String userID = record.key();
        String jsonPayload = record.value();

        try {
            List<String> data = objectMapper.readValue(jsonPayload, new TypeReference<List<String>>() {});

            if (data.size() != 2) {
                throw new IllegalArgumentException("Invalid payload format: " + data);
            }

            String orderId = data.get(0);
            double totalPrice = Double.parseDouble(data.get(1));

            // Process payment
            userService.withDrawBalance(orderId, userID, totalPrice);
        } catch (Exception e) {
            throw new RuntimeException("❌ Deserialize error: " + e.getMessage());
        }
    }

    @KafkaListener(
            topics = KafkaTopics.USER_FILL_BALANCE,
            groupId = KafkaTopics.GROUP_ID,
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
            throw new RuntimeException("❌ Deserialize error: " + e.getMessage());
        }
    }

}

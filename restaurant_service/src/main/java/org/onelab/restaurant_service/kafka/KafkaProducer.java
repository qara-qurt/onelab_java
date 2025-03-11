package org.onelab.restaurant_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.onelab.restaurant_service.utils.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void withdrawOrder(String orderId, String userId, double totalPrice) {
        try {
            String price = String.valueOf(totalPrice);
            List<String> payloadList = List.of(orderId, price);

            kafkaTemplate.send(KafkaTopics.WITHDRAW_ORDER, userId, payloadList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error:" + e.getMessage());
        }
    }
}

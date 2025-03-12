package org.onelab.restaurant_service.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.restaurant_service.service.OrderService;
import org.onelab.restaurant_service.utils.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;


    @KafkaListener(topics = KafkaTopics.CREATE_ORDER, groupId = KafkaTopics.GROUP_ID)
    public void createOrder(ConsumerRecord<String, String> record) {
        String userID = record.key();
        String jsonDishes = record.value();

        try {
            List<Long> dishIDs = objectMapper.readValue(jsonDishes, new TypeReference<List<Long>>() {});
            orderService.createOrder(Long.parseLong(userID), dishIDs);
            log.info("✅ Order created for user {} with dishes {}", userID, dishIDs);
        } catch (Exception e) {
            log.error("❌ Error creating order: {}", e.getMessage());
        }
    }
}

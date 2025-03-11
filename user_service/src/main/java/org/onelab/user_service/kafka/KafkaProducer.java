package org.onelab.user_service.kafka;

import lombok.AllArgsConstructor;
import org.onelab.user_service.utils.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void failedPaid(String orderId, String userId, double totalPrice) {
        String payload = orderId + "," + totalPrice;
        kafkaTemplate.send(KafkaTopics.FAILED_PAID, userId, payload);
    }

    public void successPaid(String orderId, String userId, double totalPrice) {
        String payload = orderId + "," + totalPrice;
        kafkaTemplate.send(KafkaTopics.SUCCESS_PAID, userId, payload);
    }
}

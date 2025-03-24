package org.onelab.order_worker.kafka;

import lombok.AllArgsConstructor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void withdrawOrder(OrderDto order, String businessKey) {
        try {
            kafkaTemplate.send(KafkaTopics.WITHDRAW_ORDER, businessKey, order);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error:" + e.getMessage());
        }
    }
}

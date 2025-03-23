package org.onelab.camunda_service.kafka;

import lombok.AllArgsConstructor;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.utils.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void withdrawOrder(OrderDto order,String businessKey) {
        try {
            kafkaTemplate.send(KafkaTopics.WITHDRAW_ORDER, businessKey, order);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error:" + e.getMessage());
        }
    }
}

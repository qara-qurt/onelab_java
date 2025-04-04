package org.onelab.user_service.kafka;

import lombok.AllArgsConstructor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void failedPaid(OrderDto order,String businessKey) {
        kafkaTemplate.send(KafkaTopics.FAILED_PAID, businessKey, order);
    }

    public void successPaid(OrderDto order,String businessKey) {
        kafkaTemplate.send(KafkaTopics.SUCCESS_PAID, businessKey, order);
    }
}

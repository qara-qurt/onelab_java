package org.onelab.gateway_cli_service.kafka;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.utils.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void fillBalance(Long userId, double balance) {
        kafkaTemplate.send(KafkaTopics.USER_FILL_BALANCE, String.valueOf(userId), balance);
    }
}

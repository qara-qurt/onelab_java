package org.onelab.gateway_cli_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final List<String> failedPayments = new ArrayList<>();
    private final List<String> successfulPayments = new ArrayList<>();

    @KafkaListener(
            topics = KafkaTopics.FAILED_PAID,
            groupId = KafkaTopics.CLI_GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFailedPayment(String message) {
        failedPayments.add("❌ PAYMENT FAILED: " + message);
    }

    @KafkaListener(
            topics = KafkaTopics.SUCCESS_PAID,
            groupId = KafkaTopics.CLI_GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSuccessfulPayment(String message) {
        successfulPayments.add("✅ PAYMENT SUCCESS: " + message);
    }

}

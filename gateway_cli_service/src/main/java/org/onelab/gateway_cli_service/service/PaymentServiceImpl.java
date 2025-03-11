package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.kafka.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final KafkaConsumer kafkaConsumer;

    public String getFailedPayments() {
        List<String> payments = kafkaConsumer.getFailedPayments();
        return payments.isEmpty() ? "✅ Нет неудачных платежей" : String.join("\n", payments);
    }
    
    public String getSuccessfulPayments() {
        List<String> payments = kafkaConsumer.getSuccessfulPayments();
        return payments.isEmpty() ? "✅ Нет успешных платежей" : String.join("\n", payments);
    }
}

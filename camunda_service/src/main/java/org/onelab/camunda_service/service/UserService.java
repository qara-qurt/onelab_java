package org.onelab.camunda_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.kafka.KafkaProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KafkaProducer kafkaProducer;
    private final CamundaMessageSender camundaMessageSender;

    public void withdrawBalance(OrderDto order,String businessKey) {
        kafkaProducer.withdrawOrder(order,businessKey);
    }

    public void failedPayment(OrderDto order,String key) {
        camundaMessageSender.sendPaymentResultMessage(key, false, order);
    }

    public void successPayment(OrderDto order,String key) {
        camundaMessageSender.sendPaymentResultMessage(key, true, order);
    }
}

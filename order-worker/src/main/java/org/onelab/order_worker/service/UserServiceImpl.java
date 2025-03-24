package org.onelab.order_worker.service;

import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.kafka.KafkaProducer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final KafkaProducer kafkaProducer;
    private final CamundaMessageSender camundaMessageSender;

    @Override
    public void withdrawBalance(OrderDto order, String businessKey) {
        kafkaProducer.withdrawOrder(order,businessKey);
    }

    @Override
    public void failedPayment(OrderDto order,String key) {
        camundaMessageSender.sendPaymentResultMessage(key, false, order);
    }
    @Override
    public void successPayment(OrderDto order,String key) {
        camundaMessageSender.sendPaymentResultMessage(key, true, order);
    }
}

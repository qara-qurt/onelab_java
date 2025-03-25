package org.onelab.order_worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.kafka.KafkaProducer;

import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private KafkaProducer kafkaProducer;
    private CamundaMessageSender camundaMessageSender;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        kafkaProducer = mock(KafkaProducer.class);
        camundaMessageSender = mock(CamundaMessageSender.class);
        userService = new UserServiceImpl(kafkaProducer, camundaMessageSender);
    }

    @Test
    void testWithdrawBalance_callsKafkaProducer() {
        OrderDto order = OrderDto.builder().id(1L).build();
        String businessKey = "ORDER-1";

        userService.withdrawBalance(order, businessKey);

        verify(kafkaProducer, times(1)).withdrawOrder(order, businessKey);
    }

    @Test
    void testFailedPayment_callsCamundaMessageSenderWithFalse() {
        OrderDto order = OrderDto.builder().id(2L).build();
        String key = "ORDER-2";

        userService.failedPayment(order, key);

        verify(camundaMessageSender, times(1))
                .sendPaymentResultMessage(key, false, order);
    }

    @Test
    void testSuccessPayment_callsCamundaMessageSenderWithTrue() {
        OrderDto order = OrderDto.builder().id(3L).build();
        String key = "ORDER-3";

        userService.successPayment(order, key);

        verify(camundaMessageSender, times(1))
                .sendPaymentResultMessage(key, true, order);
    }
}

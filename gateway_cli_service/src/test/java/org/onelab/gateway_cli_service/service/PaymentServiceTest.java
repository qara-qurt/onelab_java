package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.gateway_cli_service.kafka.KafkaConsumer;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private KafkaConsumer kafkaConsumer;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        kafkaConsumer = mock(KafkaConsumer.class);
        paymentService = new PaymentServiceImpl(kafkaConsumer);
    }

    @Test
    void testGetFailedPayments_whenEmpty_returnsSuccessMessage() {
        when(kafkaConsumer.getFailedPayments()).thenReturn(Collections.emptyList());

        String result = paymentService.getFailedPayments();

        assertEquals("✅ Нет неудачных платежей", result);
    }

    @Test
    void testGetFailedPayments_whenHasPayments_returnsFormattedList() {
        List<String> failed = List.of("❌ Платеж ID 1 не прошёл", "❌ Платеж ID 2 не прошёл");

        when(kafkaConsumer.getFailedPayments()).thenReturn(failed);

        String result = paymentService.getFailedPayments();

        assertEquals(String.join("\n", failed), result);
    }

    @Test
    void testGetSuccessfulPayments_whenEmpty_returnsSuccessMessage() {
        when(kafkaConsumer.getSuccessfulPayments()).thenReturn(Collections.emptyList());

        String result = paymentService.getSuccessfulPayments();

        assertEquals("✅ Нет успешных платежей", result);
    }

    @Test
    void testGetSuccessfulPayments_whenHasPayments_returnsFormattedList() {
        List<String> success = List.of("✅ Платеж ID 3 прошёл", "✅ Платеж ID 4 прошёл");

        when(kafkaConsumer.getSuccessfulPayments()).thenReturn(success);

        String result = paymentService.getSuccessfulPayments();

        assertEquals(String.join("\n", success), result);
    }
}

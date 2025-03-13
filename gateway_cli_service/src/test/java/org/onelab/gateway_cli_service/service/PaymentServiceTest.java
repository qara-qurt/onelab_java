//package org.onelab.gateway_cli_service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.gateway_cli_service.kafka.KafkaConsumer;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceTest {
//
//    @Mock
//    private KafkaConsumer kafkaConsumer;
//
//    @InjectMocks
//    private PaymentServiceImpl paymentService;
//
//    @BeforeEach
//    void setUp() {
//        reset(kafkaConsumer);
//    }
//
//    @Test
//    void shouldReturnFailedPayments_WhenThereAreFailedPayments() {
//        List<String> failedPayments = List.of("Payment 1 failed", "Payment 2 failed");
//        when(kafkaConsumer.getFailedPayments()).thenReturn(failedPayments);
//
//        String result = paymentService.getFailedPayments();
//
//        assertTrue(result.contains("Payment 1 failed"));
//        assertTrue(result.contains("Payment 2 failed"));
//        verify(kafkaConsumer, times(1)).getFailedPayments();
//    }
//
//    @Test
//    void shouldReturnNoFailedPayments_WhenNoFailedPaymentsExist() {
//        when(kafkaConsumer.getFailedPayments()).thenReturn(List.of());
//
//        String result = paymentService.getFailedPayments();
//
//        assertEquals("✅ Нет неудачных платежей", result);
//        verify(kafkaConsumer, times(1)).getFailedPayments();
//    }
//
//    @Test
//    void shouldReturnSuccessfulPayments_WhenThereAreSuccessfulPayments() {
//        List<String> successfulPayments = List.of("Payment 1 successful", "Payment 2 successful");
//        when(kafkaConsumer.getSuccessfulPayments()).thenReturn(successfulPayments);
//
//        String result = paymentService.getSuccessfulPayments();
//
//        assertTrue(result.contains("Payment 1 successful"));
//        assertTrue(result.contains("Payment 2 successful"));
//        verify(kafkaConsumer, times(1)).getSuccessfulPayments();
//    }
//
//    @Test
//    void shouldReturnNoSuccessfulPayments_WhenNoSuccessfulPaymentsExist() {
//        when(kafkaConsumer.getSuccessfulPayments()).thenReturn(List.of());
//
//        String result = paymentService.getSuccessfulPayments();
//
//        assertEquals("✅ Нет успешных платежей", result);
//        verify(kafkaConsumer, times(1)).getSuccessfulPayments();
//    }
//}

package org.onelab.order_worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.client.CamundaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CamundaMessageSenderTest {

    private CamundaClient camundaClient;
    private CamundaMessageSender camundaMessageSender;

    @BeforeEach
    void setUp() {
        camundaClient = mock(CamundaClient.class);
        camundaMessageSender = new CamundaMessageSender(camundaClient);
    }

    @Test
    void testSendPaymentResultMessage_success() {
        OrderDto order = OrderDto.builder().id(42L).build();
        String businessKey = "ORDER-42";

        when(camundaClient.correlateMessage(any()))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.NO_CONTENT));

        assertDoesNotThrow(() ->
                camundaMessageSender.sendPaymentResultMessage(businessKey, true, order)
        );

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(camundaClient).correlateMessage(captor.capture());

        Map<String, Object> sentPayload = captor.getValue();

        assertEquals("payment_result", sentPayload.get("messageName"));
        assertEquals("ORDER-42", sentPayload.get("businessKey"));

        Map<String, Object> processVariables = (Map<String, Object>) sentPayload.get("processVariables");
        assertNotNull(processVariables);

        Map<String, Object> paymentSuccessVar = (Map<String, Object>) processVariables.get("paymentSuccess");
        assertEquals(true, paymentSuccessVar.get("value"));
        assertEquals("Boolean", paymentSuccessVar.get("type"));

        Map<String, Object> orderIdVar = (Map<String, Object>) processVariables.get("orderId");
        assertEquals(42L, orderIdVar.get("value"));
        assertEquals("Long", orderIdVar.get("type"));
    }

    @Test
    void testSendPaymentResultMessage_whenExceptionThrown_throwsRuntimeException() {
        OrderDto order = OrderDto.builder().id(99L).build();
        String businessKey = "ORDER-99";

        when(camundaClient.correlateMessage(any()))
                .thenThrow(new RuntimeException("Camunda unreachable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                camundaMessageSender.sendPaymentResultMessage(businessKey, false, order)
        );

        assertTrue(exception.getMessage().contains("Failed to send message to Camunda"));
        verify(camundaClient).correlateMessage(any());
    }
}

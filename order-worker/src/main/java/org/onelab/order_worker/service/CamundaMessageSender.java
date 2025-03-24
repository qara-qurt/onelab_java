package org.onelab.order_worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.client.CamundaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamundaMessageSender {

    private final CamundaClient camundaClient;

    public void sendPaymentResultMessage(String businessKey, boolean paymentSuccess, OrderDto order) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageName", "payment_result");
        payload.put("businessKey", businessKey);

        Map<String, Object> processVariables = new HashMap<>();

        processVariables.put("paymentSuccess", Map.of(
                "value", paymentSuccess,
                "type", "Boolean"
        ));
        processVariables.put("orderId", Map.of(
                "value", order.getId(),
                "type", "Long"
        ));

        payload.put("processVariables", processVariables);

        try {
            ResponseEntity<String> response = camundaClient.correlateMessage(payload);
            log.info("Sent message to Camunda (businessKey={}) → Status: {}",
                    businessKey, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send message to Camunda", e);
            throw new RuntimeException("Failed to send message to Camunda", e);
        }
    }
}

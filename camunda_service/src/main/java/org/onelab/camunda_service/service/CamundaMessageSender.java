package org.onelab.camunda_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.camunda_service.dto.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamundaMessageSender {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${camunda.rest.url}")
    private String camundaRestUrl;

    public void sendPaymentResultMessage(String businessKey, boolean paymentSuccess, OrderDto order) {
        String url = camundaRestUrl + "/message";

        Map<String, Object> payload = new HashMap<>();
        payload.put("messageName", "payment_result");
        payload.put("businessKey", businessKey);

        Map<String, Object> processVariables = new HashMap<>();

        Map<String, Object> successVar = new HashMap<>();
        successVar.put("value", paymentSuccess);
        successVar.put("type", "Boolean");
        processVariables.put("paymentSuccess", successVar);

        Map<String, Object> orderIdVar = new HashMap<>();
        orderIdVar.put("value", order.getId());
        orderIdVar.put("type", "Long");
        processVariables.put("orderId", orderIdVar);

        payload.put("processVariables", processVariables);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);
            log.info("Correlation sent to Camunda for businessKey={} with status {}", businessKey, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send message to Camunda", e);
            throw new RuntimeException("Failed to send message to Camunda", e);
        }
    }
}

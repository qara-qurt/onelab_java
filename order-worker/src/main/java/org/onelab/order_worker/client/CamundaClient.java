package org.onelab.order_worker.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "camunda-service")
public interface CamundaClient {
    @PostMapping("/engine-rest/message")
    ResponseEntity<String> correlateMessage(@RequestBody Map<String, Object> body);
}


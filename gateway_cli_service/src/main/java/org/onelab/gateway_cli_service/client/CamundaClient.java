package org.onelab.gateway_cli_service.client;

import org.onelab.common_lib.dto.OrderRequestDto;
import org.onelab.gateway_cli_service.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "camunda-service", configuration = ClientConfig.class)
public interface CamundaClient {

    @PostMapping("/api/process/start/create-order")
    String createOrder(@RequestBody OrderRequestDto orderRequestDto);

}

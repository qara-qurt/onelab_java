package org.onelab.camunda_service.client;

import org.onelab.camunda_service.config.FeignConfig;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.dto.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "restaurant-service", configuration = FeignConfig.class, url = "http://localhost:8083")
public interface RestaurantClient {
    @PostMapping("/api/orders")
    OrderDto createOrder(@RequestBody OrderRequestDto orderRequest);
}

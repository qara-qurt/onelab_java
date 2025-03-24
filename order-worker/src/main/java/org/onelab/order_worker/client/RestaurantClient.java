package org.onelab.order_worker.client;

import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.dto.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "restaurant-service")
public interface RestaurantClient {
    @PostMapping("/api/orders")
    OrderDto createOrder(@RequestBody OrderRequestDto orderRequest);
}

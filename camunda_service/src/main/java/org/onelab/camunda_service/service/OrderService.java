package org.onelab.camunda_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.camunda_service.client.RestaurantClient;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.dto.OrderRequestDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RestaurantClient restaurantClient;

    public OrderDto createOrder(OrderRequestDto orderRequestDto) {
        try {
            return  restaurantClient.createOrder(orderRequestDto);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating order: " + e.getMessage());
        }
    }
}

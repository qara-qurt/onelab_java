package org.onelab.order_worker.service;

import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.dto.OrderRequestDto;
import org.onelab.order_worker.client.RestaurantClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RestaurantClient restaurantClient;

    @Override
    public OrderDto createOrder(Long userId, List<Long> dishIds) {
        OrderRequestDto request = new OrderRequestDto(userId, dishIds);
        return restaurantClient.createOrder(request);
    }
}

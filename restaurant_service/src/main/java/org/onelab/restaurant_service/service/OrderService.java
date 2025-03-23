package org.onelab.restaurant_service.service;

import org.onelab.restaurant_service.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(Long userId, List<Long> dishIds);
    OrderDto getOrder(Long id);
    List<OrderDto> getOrdersByUser(Long userId, int page, int size);
    List<OrderDto> getOrders(int page, int size);
    OrderDto updateOrder(OrderDto orderDto);
}

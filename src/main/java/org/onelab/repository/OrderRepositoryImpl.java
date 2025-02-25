package org.onelab.repository;

import org.onelab.dto.OrderDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final Map<Long, OrderDto> orders = new HashMap<>();

    public void save(OrderDto order) {
        orders.put(order.getId(), order);
    }

    public OrderDto findById(Long id) {
        return orders.get(id);
    }

    @Override
    public List<OrderDto> findAll() {
        return new ArrayList<>(orders.values());
    }

    public List<OrderDto> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(order -> order.getCustomer().getId().equals(userId))
                .collect(Collectors.toList());
    }
}

package org.onelab.repository;

import org.onelab.dto.OrderDto;

import java.util.List;

public interface OrderRepository {
    void save(OrderDto orderDto);
    OrderDto findById(Long id);
    List<OrderDto> findAll();
}

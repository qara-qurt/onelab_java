package org.onelab.repository;

import org.onelab.entity.Order;

import java.util.List;

public interface OrderRepository {
    void save(Order orderDto);
    Order findById(Long id);
    List<Order> findAll();
    List<Order> findByUserId(Long userId);
}

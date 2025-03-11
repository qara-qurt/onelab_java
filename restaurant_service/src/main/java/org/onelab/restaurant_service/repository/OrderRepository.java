package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.Order;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends ElasticsearchRepository<Order, String> {
    List<Order> findByStatus(OrderStatus status);
}

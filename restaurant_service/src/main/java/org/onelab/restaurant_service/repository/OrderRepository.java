package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.OrderEntity;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);
    List<OrderEntity> findByStatus(OrderStatus status);
}

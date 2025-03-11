package org.onelab.gateway_cli_service.repository;

import org.onelab.gateway_cli_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends ElasticsearchRepository<Order, String> {

    Optional<Order> findById(String orderId);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

}

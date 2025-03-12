package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.OrderDocument;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderElasticRepository extends ElasticsearchRepository<OrderDocument, String> {
    List<OrderDocument> findByStatus(OrderStatus status);
}

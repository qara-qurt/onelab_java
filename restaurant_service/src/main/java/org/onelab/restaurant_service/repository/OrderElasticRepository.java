package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.OrderDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrderElasticRepository extends ElasticsearchRepository<OrderDocument, Long> {
}

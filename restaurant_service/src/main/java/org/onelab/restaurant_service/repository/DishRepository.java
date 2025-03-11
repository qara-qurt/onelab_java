package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.Dish;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DishRepository extends ElasticsearchRepository<Dish, String> {
    Optional<Dish> findByName(String name);
}

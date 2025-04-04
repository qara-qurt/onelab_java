package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<DishEntity, Long> {
    Optional<DishEntity> findByName(String name);
    List<DishEntity> findByUpdatedAtAfter(Instant updatedAt);
}

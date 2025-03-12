package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    boolean existsByDishesContaining(DishEntity dish);
    boolean existsByName(String name);
}

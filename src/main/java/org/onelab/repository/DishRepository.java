package org.onelab.repository;

import org.onelab.entity.Dish;

import java.util.List;

public interface DishRepository {
    void save(Dish dishDto);
     List<Dish> findAll();
}

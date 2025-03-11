package org.onelab.restaurant_service.service;

import org.onelab.restaurant_service.entity.Dish;

public interface DishService {
    String save(Dish dish);
    void remove(String id);
}

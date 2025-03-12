package org.onelab.restaurant_service.service;

import org.onelab.restaurant_service.dto.DishDto;

import java.util.List;

public interface DishService {
    Long save(DishDto dish);
    void remove(Long id);
    List<DishDto> getDishes(int page, int size);
    DishDto getDishById(Long id);
    List<DishDto> searchDishes(String text, int page, int size);
}

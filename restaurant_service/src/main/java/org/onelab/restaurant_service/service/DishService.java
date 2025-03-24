package org.onelab.restaurant_service.service;

import org.onelab.common_lib.dto.DishDto;

import java.util.List;

public interface DishService {
    Long save(DishDto dish);
    void remove(Long id);
    List<DishDto> getDishes(int page, int size);
    DishDto getDishById(Long id);
    List<DishDto> searchDishes(String text, int page, int size);
}

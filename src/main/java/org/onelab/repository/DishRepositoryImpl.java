package org.onelab.repository;

import org.onelab.dto.DishDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DishRepositoryImpl implements DishRepository {
    private final Map<Long, DishDto> dishes = new HashMap<>();

    public void save(DishDto dish) {
        dishes.put(dish.getId(), dish);
    }

    public DishDto findById(Long id) {
        return dishes.get(id);
    }

    @Override
    public List<DishDto> findAll() {
        return dishes.values().stream().toList();
    }
}

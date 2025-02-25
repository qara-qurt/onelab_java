package org.onelab.repository;

import org.onelab.dto.DishDto;

import java.util.List;

public interface DishRepository {
    void save(DishDto dishDto);
    DishDto findById(Long id);
    List<DishDto> findAll();
}

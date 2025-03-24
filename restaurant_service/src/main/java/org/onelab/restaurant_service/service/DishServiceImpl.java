package org.onelab.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.restaurant_service.entity.DishDocument;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.mapper.DishMapper;
import org.onelab.restaurant_service.repository.DishElasticRepository;
import org.onelab.restaurant_service.repository.DishRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final DishElasticRepository dishElasticRepository;

    @Override
    public Long save(DishDto dishDto) {
        Optional<DishEntity> existingDish = dishRepository.findByName(dishDto.getName());
        if (existingDish.isPresent()) {
            throw new AlreadyExistException("Dish with this name already exists");
        }

        DishEntity dish = DishMapper.toEntity(dishDto);
        Long id = dishRepository.save(dish).getId();
        syncDishToElastic(dish);

        return id;
    }

    @Override
    public void remove(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new NotFoundException("Dish not found");
        }
        dishRepository.deleteById(id);
        dishElasticRepository.deleteById(String.valueOf(id));
    }

    @Override
    public List<DishDto> getDishes(int page, int size) {
        return dishRepository.findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(DishMapper::toDto)
                .toList();
    }

    @Override
    public DishDto getDishById(Long id) {
        Optional<DishEntity> dish = dishRepository.findById(id);
        if (dish.isEmpty()) {
            throw new NotFoundException("Dish not found");
        }
        return DishMapper.toDto(dish.get());
    }

    @Override
    public List<DishDto> searchDishes(String text, int page, int size) {
        return dishElasticRepository.searchByNameOrDescription(text, PageRequest.of(page - 1, size))
                .stream()
                .map(DishMapper::toDto)
                .toList();
    }

    private void syncDishToElastic(DishEntity dish) {
        DishDocument dishDocument = DishMapper.toDocument(dish);
        dishElasticRepository.save(dishDocument);
    }
}

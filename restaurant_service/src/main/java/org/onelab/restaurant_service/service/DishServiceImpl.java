package org.onelab.restaurant_service.service;

import lombok.AllArgsConstructor;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    @Override
    public String save(Dish dish) {
        Optional<Dish> existDish = dishRepository.findByName(dish.getName());
        if (existDish.isPresent()) {
            throw new AlreadyExistException("Dish with name " + dish.getName() + " already exist.");
        }

        return dishRepository.save(dish).getId();
    }

    @Override
    public void remove(String id) {
        Optional<Dish> existDish = dishRepository.findById(id);
        if (existDish.isEmpty()) {
            throw new NotFoundException("Dish with id " + id + " not found");
        }

        dishRepository.delete(existDish.get());
    }
}

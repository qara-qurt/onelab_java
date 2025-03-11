package org.onelab.restaurant_service.service;

import lombok.AllArgsConstructor;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.entity.Menu;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;

    @Override
    public String addMenu(Menu menu) {
        Optional<Menu> existMenu = menuRepository.findByName(menu.getName());
        if (existMenu.isPresent()) {
            throw new AlreadyExistException("Menu with this name " + menu.getName() + " already exist");
        }
        return menuRepository.save(menu).getId();
    }

    @Override
    public void removeMenu(String id) {
        Optional<Menu> existMenu = menuRepository.findById(id);
        if (existMenu.isEmpty()) {
            throw new NotFoundException("Menu with ID '" + id + "' not found.");
        }
        menuRepository.deleteById(id);
    }

    @Override
    public void addDishesToMenu(String menuID, List<String> dishIDs) {
        Optional<Menu> existMenu = menuRepository.findById(menuID);
        if (existMenu.isEmpty()) {
            throw new NotFoundException("Menu with ID '" + menuID + "' not found.");
        }

        Menu menu = existMenu.get();

        List<Dish> foundDishes = (List<Dish>) dishRepository.findAllById(dishIDs);

        if (foundDishes.size() != dishIDs.size()) {
            throw new NotFoundException("Some dishes were not found.");
        }

        List<Dish> existingDishes = menu.getDishes();
        List<Dish> newDishes = foundDishes.stream()
                .filter(dish -> existingDishes.stream().noneMatch(d -> d.getId().equals(dish.getId())))
                .toList();

        if (newDishes.isEmpty()) {
            throw new AlreadyExistException("All dishes already exist in the menu.");
        }

        existingDishes.addAll(newDishes);
        menu.setDishes(existingDishes);

        menuRepository.save(menu);
    }

    @Override
    public void removeDishesFromMenu(String menuID, List<String> dishIDs) {
        Optional<Menu> existMenu = menuRepository.findById(menuID);
        if (existMenu.isEmpty()) {
            throw new NotFoundException("Menu with ID '" + menuID + "' not found.");
        }

        Menu menu = existMenu.get();

        List<Dish> existingDishes = menu.getDishes();
        if (existingDishes.isEmpty()) {
            throw new IllegalArgumentException("Menu is empty.");
        }

        List<Dish> updatedDishes = existingDishes.stream()
                .filter(dish -> !dishIDs.contains(dish.getId()))
                .toList();

        if (updatedDishes.size() == existingDishes.size()) {
            throw new IllegalArgumentException("No dishes were removed");
        }

        menu.setDishes(updatedDishes);
        menuRepository.save(menu);
    }

}

package org.onelab.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.restaurant_service.dto.DishDto;
import org.onelab.restaurant_service.dto.MenuDto;
import org.onelab.restaurant_service.dto.MenuRequestDto;
import org.onelab.restaurant_service.entity.MenuEntity;
import org.onelab.restaurant_service.entity.MenuDocument;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.mapper.MenuMapper;
import org.onelab.restaurant_service.repository.MenuRepository;
import org.onelab.restaurant_service.repository.MenuElasticRepository;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.DishElasticRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuElasticRepository menuElasticRepository;
    private final DishRepository dishRepository;
    private final DishElasticRepository dishElasticRepository;

    @Override
    @Transactional
    public String createMenu(MenuRequestDto menuRequestDto) {
        if (menuRepository.existsByName(menuRequestDto.getName())) {
            throw new AlreadyExistException("Menu with this name already exists.");
        }

        List<DishEntity> dishes = dishRepository.findAllById(menuRequestDto.getDishIds());

        if (dishes.isEmpty()) {
            throw new NotFoundException("No valid dishes found.");
        }

        List<DishEntity> uniqueDishes = dishes.stream()
                .filter(dish -> !menuRepository.existsByDishesContaining(dish))
                .toList();

        if (uniqueDishes.isEmpty()) {
            throw new AlreadyExistException("All selected dishes are already in a menu.");
        }

        MenuEntity menu = MenuEntity.builder()
                .name(menuRequestDto.getName())
                .dishes(uniqueDishes)
                .build();

        MenuEntity savedMenu = menuRepository.save(menu);

        return savedMenu.getId().toString();
    }


    @Override
    @Transactional
    public void removeMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new NotFoundException("Menu not found.");
        }
        menuRepository.deleteById(id);
        menuElasticRepository.deleteById(id.toString());
    }

    @Override
    @Transactional
    public void addDishesToMenu(Long menuId, List<Long> dishIds) {
        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu not found."));

        List<DishEntity> dishes = dishRepository.findAllById(dishIds);

        if (dishes.isEmpty()) {
            throw new NotFoundException("No valid dishes found.");
        }

        List<Long> existingDishIds = menu.getDishes().stream()
                .map(DishEntity::getId)
                .toList();

        List<DishEntity> newDishes = dishes.stream()
                .filter(dish -> !existingDishIds.contains(dish.getId()))
                .toList();

        if (newDishes.isEmpty()) {
            throw new AlreadyExistException("All selected dishes are already in the menu.");
        }

        menu.getDishes().addAll(newDishes);
        menuRepository.save(menu);
    }

    @Override
    @Transactional
    public void removeDishesFromMenu(Long menuId, List<Long> dishIds) {
        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu not found."));

        List<Long> currentDishIds = menu.getDishes().stream()
                .map(DishEntity::getId)
                .toList();

        List<Long> removableDishes = dishIds.stream()
                .filter(currentDishIds::contains)
                .toList();

        if (removableDishes.isEmpty()) {
            throw new NotFoundException("None of the selected dishes are in the menu.");
        }

        menu.getDishes().removeIf(dish -> removableDishes.contains(dish.getId()));

        menuRepository.save(menu);
    }



    @Override
    public MenuDto getMenu(Long id) {
        return menuRepository.findById(id)
                .map(MenuMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Menu not found."));
    }

    @Override
    public List<MenuDto> getMenus(int page, int size) {
        return menuRepository.findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(MenuMapper::toDto)
                .toList();
    }

}

package org.onelab.gateway_cli_service.service;

import lombok.AllArgsConstructor;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Menu;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.DishRepository;
import org.onelab.gateway_cli_service.repository.MenuRepository;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class MenuServiceImpl implements MenuService{

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    public String getMenu(String id) {
        Optional<Menu> menuOpt = menuRepository.findById(id);
        if (menuOpt.isEmpty()) {
            return ("❌ Меню с ID '" + id + "' не найдено.");
        }

        Menu menu = menuOpt.get();

        List<Dish> dishes = menu.getDishes().stream()
                .map(dish -> {
                    if (dish.getId() == null) {
                        return dishRepository.findByName(dish.getName()).orElse(dish);
                    }
                    return dish;
                })
                .toList();

        String dishesList = dishes.isEmpty() ? "📭 Блюда отсутствуют." :
                dishes.stream()
                        .map(Utils::formatDish)
                        .collect(Collectors.joining("\n"));

        return """
           📜 Меню: %s
           %s
           """.formatted(menu.getName(), dishesList);
    }



    @Override
    public String createMenu(String name, List<String> dishesID) {
        Optional<Menu> existMenu = menuRepository.findByName(name);
        if (existMenu.isPresent()) {
            return "❌ Меню с именем '" + name + "' уже существует.";
        }
        dishesID.forEach(dishID -> {
            Optional<Dish> dish = dishRepository.findById(dishID);
            if (dish.isEmpty()) {
                throw new IllegalArgumentException("❌ Блюдо с ID '" + dishID + "' не найдено.");
            }
        });

        Menu menu = Menu.builder()
                .name(name)
                .dishes(dishesID.stream()
                        .map(dishID -> dishRepository.findById(dishID).get())
                        .collect(Collectors.toList()))
                .build();

        kafkaProducer.addMenu(menu);
        return "🔧 Создание меню: " + name;

    }

    @Override
    public String removeMenu(String id) {
        Optional<Menu> existMenu = menuRepository.findById(id);
        if (existMenu.isEmpty()) {
            throw new IllegalArgumentException("❌ Меню с ID '" + id + "' не найдено.");
        }

        kafkaProducer.removeMenu(existMenu.get().getId());
        return "🗑️ Удаление меню с ID: " + id;
    }

    @Override
    public String getMenus(int page, int size) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        List<Menu> menus = StreamSupport.stream(menuRepository.findAll(pageRequest).spliterator(), false).toList();
        if (menus.isEmpty()) {
            return "📭 Меню отсутствуют.";
        }
        return menus.stream()
                .map(Utils::formatMenu)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String addDishToMenu(String menuID, List<String> dishIDs) {
        Optional<Menu> existMenu = menuRepository.findById(menuID);
        if (existMenu.isEmpty()) {
            throw new IllegalArgumentException("❌ Меню с ID '" + menuID + "' не найдено.");
        }

        Menu menu = existMenu.get();

        List<Dish> foundDishes = (List<Dish>) dishRepository.findAllById(dishIDs);

        if (foundDishes.size() != dishIDs.size()) {
            throw new IllegalArgumentException("❌ Некоторые блюда не найдены.");
        }

        List<Dish> existingDishes = menu.getDishes();
        List<Dish> newDishes = foundDishes.stream()
                .filter(dish -> existingDishes.stream().noneMatch(d -> d.getId().equals(dish.getId())))
                .toList();

        if (newDishes.isEmpty()) {
            throw new IllegalArgumentException("❌ Все блюда уже добавлены в меню.");
        }

        kafkaProducer.addDishToMenu(menu, dishIDs);

        return "🔧 Добавление блюд в меню: " + menuID;
    }


    @Override
    public String removeDishFromMenu(String menuID, List<String> dishIDs) {
        Optional<Menu> existMenu = menuRepository.findById(menuID);
        if (existMenu.isEmpty()) {
            throw new IllegalArgumentException("❌ Меню с ID '" + menuID + "' не найдено.");
        }

        Menu menu = existMenu.get();

        for (Dish dish : menu.getDishes()) {
            System.out.println("Dish: " + dish.getName() + " | ID: " + dish.getId());
        }
        menu.getDishes().forEach(dish -> {
            if (dish.getId() == null) {
                throw new IllegalStateException("❌ У блюда '" + dish.getName() + "' нет ID в базе.");
            }
        });

        kafkaProducer.removeDishFromMenu(menu, dishIDs);
        return "🗑️ Удаление блюд из меню: " + menuID;
    }

}

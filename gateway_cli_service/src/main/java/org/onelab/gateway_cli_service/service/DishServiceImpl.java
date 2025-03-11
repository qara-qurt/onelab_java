package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.DishRepository;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final KafkaProducer kafkaProducer;

    @Override
    public String getDishes(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Dish> dishes = StreamSupport.stream(dishRepository.findAll(pageRequest).spliterator(), false)
                .toList();

        if (dishes.isEmpty()) {
            return "❌ Нету блюд";
        }

        return dishes.stream()
                .map(Utils::formatDish)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String addDish(String name, String description, double price) {
        Optional<Dish> existDish = dishRepository.findByName(name);
        if (existDish.isPresent()) {
            throw new IllegalArgumentException("❌ Блюдо с названием " + name + " уже существует.");
        }

        Dish dish = Dish.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();

        kafkaProducer.addDish(dish);
        return ("✅ Блюдо \"" + dish.getName() + "\" будет добавлена за kz - " + dish.getPrice());
    }

    @Override
    public String removeDish(String id) {
        Optional<Dish> existMenu = dishRepository.findById(id);
        if (existMenu.isEmpty()) {
            throw new IllegalArgumentException("❌ Блюдо с ID " + id + " нету.");
        }

        kafkaProducer.removeDish(id);
        return ("✅ Блюдо с ID " + id + " будет удалено.");
    }

    @Override
    public String searchDishes(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Dish> dishPage = dishRepository.searchByFields(name, pageable);
        List<Dish> dishes = dishPage.getContent();

        if (dishes.isEmpty()) {
            return "❌ Блюлдо с параметром '" + name + "' не найден.";
        }

        return dishes.stream()
                .map(Utils::formatDish)
                .collect(Collectors.joining("\n"));
    }
}

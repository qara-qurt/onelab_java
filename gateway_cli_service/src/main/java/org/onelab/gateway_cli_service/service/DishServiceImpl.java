package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.dto.DishDto;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final RestaurantClient restaurantClient;

    @Override
    public String getDishes(int page, int size) {
        List<DishDto> dishes = restaurantClient.getDishes(page, size);

        if (dishes.isEmpty()) {
            return "❌ Нет доступных блюд";
        }

        return dishes.stream()
                .map(Utils::formatDish)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String addDish(String name, String description, double price) {
        DishDto newDish = DishDto.builder()
                .name(name)
                .description(description)
                .price(price).build();

        try {
            Map<String, Long> response = restaurantClient.addDish(newDish);
            Long dishId = response.get("id");
            return "✅ Блюдо добавлено! ID: " + dishId;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
                return "❌ Блюдо с таким названием уже существует.";
            }
            throw ex;
        }
    }

    @Override
    public String removeDish(Long id) {
        try {
            restaurantClient.removeDish(id);
            return "✅ Блюдо с ID " + id + " удалено.";
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                return "❌ Блюдо с ID " + id + " не найдено.";
            }
            throw ex;
        }
    }

    @Override
    public String searchDishes(String name, int page, int size) {
        List<DishDto> dishes = restaurantClient.searchDishes(name, page, size);

        if (dishes.isEmpty()) {
            return "❌ Блюдо с параметром '" + name + "' не найдено.";
        }

        return dishes.stream()
                .map(Utils::formatDish)
                .collect(Collectors.joining("\n"));
    }
}

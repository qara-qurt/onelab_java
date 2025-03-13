package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.dto.MenuDto;
import org.onelab.gateway_cli_service.dto.MenuRequestDto;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final RestaurantClient restaurantClient;

    @Override
    public String getMenu(Long id) {
        try {
            MenuDto menu = restaurantClient.getMenu(id);
            return Utils.formatMenu(menu);
        } catch (Exception e) {
            return "❌ Меню с ID '" + id + "' не найдено.";
        }
    }

    @Override
    public String createMenu(String name, List<Long> dishIDs) {
        String validationError = Utils.validateMenuInput(name, dishIDs);
        if (validationError != null) return validationError;

        MenuRequestDto menuRequest = new MenuRequestDto(name, dishIDs);
        try {
            String menuId = restaurantClient.addMenu(menuRequest);
            return "✅ Меню '" + name + "' создано. ID: " + menuId;
        } catch (Exception e) {
            return "❌ Ошибка при создании меню: " + e.getMessage();
        }
    }

    @Override
    public String removeMenu(Long id) {
        try {
            restaurantClient.removeMenu(id);
            return "🗑️ Меню с ID '" + id + "' удалено.";
        } catch (Exception e) {
            return "❌ Ошибка при удалении меню: " + e.getMessage();
        }
    }

    @Override
    public String getMenus(int page, int size) {
        List<MenuDto> menus = restaurantClient.getMenus(page, size);
        if (menus.isEmpty()) {
            return "📭 Меню отсутствуют.";
        }
        return menus.stream()
                .map(Utils::formatMenu)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String addDishToMenu(Long menuID, List<Long> dishIDs) {
        try {
            restaurantClient.addDishesToMenu(menuID, dishIDs);
            return "✅ Добавлены блюда в меню: " + menuID;
        } catch (Exception e) {
            return "❌ Ошибка при добавлении блюд в меню: " + e.getMessage();
        }
    }

    @Override
    public String removeDishFromMenu(Long menuID, List<Long> dishIDs) {
        try {
            restaurantClient.removeDishesFromMenu(menuID, dishIDs);
            return "🗑️ Удалены блюда из меню: " + menuID;
        } catch (Exception e) {
            return "❌ Ошибка при удалении блюд из меню: " + e.getMessage();
        }
    }
}

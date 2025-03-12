package org.onelab.gateway_cli_service.shell;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.dto.DishDto;
import org.onelab.gateway_cli_service.dto.MenuDto;
import org.onelab.gateway_cli_service.service.MenuService;
import org.onelab.gateway_cli_service.service.RestaurantClient;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class MenuCLI {

    private final MenuService menuService;
    private final RestaurantClient restaurantClient;

    @ShellMethod(key = "get-menu", value = "Просмотреть меню: get-menu")
    public String getMenu(@ShellOption String id) {
        return menuService.getMenu(id);
    }

    @ShellMethod(key = "get-menus", value = "Просмотреть все меню: get-menus")
    public String getMenus(@ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
            List<MenuDto> menus = restaurantClient.getMenus(page, size);
            return menus.toString();

//        return menuService.getMenus(page, size);
    }

    @ShellMethod(key = "add-menu",value = "Добавить меню: add-menu <Название>")
    public String addMenu(@ShellOption String name,@ShellOption List<String> dishIDs) {
        String validationError = Utils.validateMenuInput(name, dishIDs);
        if (validationError != null) return validationError;

       return menuService.createMenu(name,dishIDs);
    }

    @ShellMethod(key = "remove-menu",value = "Удалить меню: remove-menu <ID>")
    public String removeMenu(@ShellOption String id) {
        return menuService.removeMenu(id);
    }

    @ShellMethod(key = "add-dish-to-menu", value = "Добавить блюда в меню: add-dish-to-menu <DishID,DishID...>")
    public String addDishToMenu(@ShellOption String menuID, @ShellOption List<String> dishIDs) {
        return menuService.addDishToMenu(menuID,dishIDs);
    }

    @ShellMethod(key = "remove-dish-from-menu", value = "Удалить блюда из меню: remove-dish-from-menu --dish=<DishID> --menu=<MenuID>")
    public String removeDishFromMenu(@ShellOption List<String> dishIDs, @ShellOption String menuID) {
        return menuService.removeDishFromMenu(menuID,dishIDs);
    }
}

package org.onelab.gateway_cli_service.shell;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.service.DishService;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class DishCLI {

    private final DishService dishService;

    @ShellMethod(key = "get-dishes", value = "Просмотреть блюда: get-dishes")
    public String getDishes(@ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        return dishService.getDishes(page,size);
    }

    @ShellMethod(key = "add-dish",value = "Добавить блюдо: add-dish <Название> <Цена>")
    public String addMenuItem(@ShellOption String name, @ShellOption String description, @ShellOption double price) {
        String validationError = Utils.validateDish(name, description, price);
        if (validationError != null) {
            return validationError;
        }

        return dishService.addDish(name, description, price);
    }

    @ShellMethod(key = "remove-dish",value = "Удалить блюдо: remove-dish <ID>")
    public String removeFromDish(@ShellOption Long id) {
        return dishService.removeDish(id);
    }

    @ShellMethod(key = "search-dishes", value = "Поиск пользователей: search-dishes <Название>")
    public String searchDish(@ShellOption String name, @ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        name = name.trim();
        return dishService.searchDishes(name,page,size);
    }
}

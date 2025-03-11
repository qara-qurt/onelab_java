package org.onelab.gateway_cli_service.shell;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.service.UserService;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class UserCLI {

    private final UserService userService;

    @ShellMethod(key = "get-user",value = "Просмотреть пользователя: get-user <ID>")
    public String getUser(@ShellOption String id) {
        return userService.getUserByID(id.trim());
    }

    @ShellMethod(key = "create-user", value = "Создать пользователя: create-user --name <Имя> --surname <Фамилия> --username <Логин> --phone <Телефон> --password <Пароль>")
    public String createUser(
            @ShellOption(value = "--name", help = "Имя пользователя") String name,
            @ShellOption(value = "--surname", help = "Фамилия пользователя") String surname,
            @ShellOption(value = "--username", help = "Уникальное имя пользователя") String username,
            @ShellOption(value = "--phone", help = "Телефон пользователя") String phone,
            @ShellOption(value = "--password", help = "Пароль пользователя") String password
    ) {
        name = name.trim();
        surname = surname.trim();
        username = username.trim();
        phone = phone.trim();
        password = password.trim();

        String validationError = Utils.validateUserInput(name, surname, username, phone, password);
        if (validationError != null) {
            return validationError;
        }
        return userService.createUser(name, surname, username, phone, password);
    }

    @ShellMethod(key = "search-user", value = "Поиск пользователей: search-users <Имя>")
    public String searchUsers(@ShellOption String name, @ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        name = name.trim();
        return userService.searchUsers(name,page,size);
    }

    @ShellMethod(key = "get-users", value = "Поиск пользователей: get-users <page> <size>")
    public String getUsers( @ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        return userService.getUsers(page,size);
    }

    @ShellMethod(key = "remove-user", value = "Удалить пользователя: remove-user <ID>")
    public String removeUser(@ShellOption String id) {
        return userService.removeUser(id.trim());
    }

    @ShellMethod(key = "fill-balance", value = "Пополнить баланс: fill-balance <userId> <amount>")
    public String fillBalance(@ShellOption String userId, @ShellOption double amount) {
        if (amount <= 0) {
            return "❌ Сумма должна быть больше нуля.";
        }

        return userService.fillBalance(userId, amount);
    }
}

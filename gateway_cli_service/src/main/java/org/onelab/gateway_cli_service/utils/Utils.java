package org.onelab.gateway_cli_service.utils;

import org.onelab.common_lib.dto.*;

import java.util.List;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я0-9\\s-]{2,50}$");

    private Utils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static String formatUser(UserDto user) {
        return """
               👤 %s %s (@%s)
               📌 ID: %d | 📱 %s
               💰 Баланс: %.2f KZT | 🔒 Активен: %s
               """.formatted(
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getId(),
                user.getPhone(),
                user.getBalance(),
                user.isActive() ? "✅ Да" : "❌ Нет"
        );
    }

    public static String formatDish(DishDto dish) {
        return """
               🍽️ %s | 📌 ID: %d
               💰 Цена: %.2f KZT
               📝 %s
               """.formatted(
                dish.getName(),
                dish.getId(),
                dish.getPrice(),
                dish.getDescription()
        );
    }

    public static String formatMenu(MenuDto menu) {
        String dishesList = menu.getDishes().isEmpty() ? "📭 Нет блюд." :
                menu.getDishes().stream()
                        .map(DishDto::getName)
                        .toList()
                        .toString();

        return """
               📜 Меню: %s | 📌 ID: %d
               🍽 Блюда: %s
               """.formatted(menu.getName(), menu.getId(), dishesList);
    }

    public static String formatOrder(OrderDto order) {
        String dishes = String.join(", ", order.getDishes().stream().map(DishDto::getName).toList());

        return """
               📦 Заказ #%d | 👤 Клиент: %d
               🍽 %s
               💰 %.2f KZT | 📌 Статус: %s
               """.formatted(order.getId(), order.getCustomerId(), dishes, order.getTotalPrice(), order.getStatus());
    }

    public static String validateUserInput(String name, String surname, String username, String phone, String password) {
        StringBuilder errors = new StringBuilder();

        if (name == null || name.trim().isEmpty()) errors.append("❌ Имя не может быть пустым.\n");
        if (surname == null || surname.trim().isEmpty()) errors.append("❌ Фамилия не может быть пустой.\n");
        if (username == null || username.trim().isEmpty()) errors.append("❌ Логин не может быть пустым.\n");
        if (phone == null || !phone.matches("^\\+[0-9]{10,15}$"))
            errors.append("❌ Некорректный номер телефона.\n");
        if (password == null || password.length() < 6)
            errors.append("❌ Пароль должен быть не менее 6 символов.\n");

        return errors.isEmpty() ? null : errors.toString();
    }

    public static String validateDish(String name, String description, double price) {
        StringBuilder errors = new StringBuilder();

        if (name == null || name.trim().isEmpty()) errors.append("❌ Название блюда не может быть пустым.\n");
        if (description == null || description.trim().isEmpty()) errors.append("❌ Описание не может быть пустым.\n");
        if (price <= 0) errors.append("❌ Цена должна быть больше 0.\n");

        return errors.isEmpty() ? null : errors.toString();
    }

    public static String validateMenuInput(String name, List<Long> dishIDs) {
        StringBuilder errors = new StringBuilder();

        if (name == null || !NAME_PATTERN.matcher(name).matches())
            errors.append("❌ Ошибка: Некорректное название меню!\n");
        if (dishIDs == null || dishIDs.isEmpty())
            errors.append("❌ Меню должно содержать хотя бы одно блюдо.\n");

        return errors.isEmpty() ? null : errors.toString();
    }
}

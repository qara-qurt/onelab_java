package org.onelab.gateway_cli_service.utils;

import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Menu;
import org.onelab.gateway_cli_service.entity.Order;
import org.onelab.gateway_cli_service.entity.User;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я0-9\\s-]{2,50}$");

    private Utils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static String formatUser(User user) {
        return """
               👤 Пользователь: %s %s
               👤 Username: %s
               📌 ID: %s
               📱 Телефон: %s
               💰 Баланс: %.2f kz
               🔒 Активен: %s
               """.formatted(
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getId(),
                user.getPhone(),
                user.getBalance(),
                user.isActive() ? "Да" : "Нет"
        );
    }

    public static String formatDish(Dish dish) {
        return """
               🍽️ Блюдо: %s
               📌 ID: %s
               💰 Цена: %.2f kz
               📝 Описание: %s
               """.formatted(
                dish.getName(),
                dish.getId(),
                dish.getPrice(),
                dish.getDescription()
        );
    }

    public static String formatMenu(Menu menu) {
        return """
               🍽️ Меню: %s
               📌 ID: %s
               """.formatted(
                menu.getName(),
                menu.getId()
        );
    }

    public static String formatOrder(Order order) {
        String dishes = order.getDishes().stream()
                .map(Dish::getName)
                .collect(Collectors.joining(", "));

        return "📦 Заказ ID: " + order.getId() + "\n" +
                "👤 Клиент: " + order.getCustomerId() + "\n" +
                "🍽️ Блюда: [" + dishes + "]\n" +
                "💰 Цена: " + order.getTotalPrice() + " KZT\n" +
                "📌 Статус: " + order.getStatus();
    }

    public static String validateUserInput(String name, String surname, String username, String phone, String password) {
        if (name == null || name.trim().isEmpty()) {
            return "❌ Ошибка: Имя не может быть пустым.";
        }
        if (surname == null || surname.trim().isEmpty()) {
            return "❌ Ошибка: Фамилия не может быть пустой.";
        }
        if (username == null || username.trim().isEmpty()) {
            return "❌ Ошибка: Логин не может быть пустым.";
        }
        if (!phone.matches("^\\+[0-9]{10,15}$")) {
            return "❌ Ошибка: Некорректный номер телефона. Должен начинаться с + и содержать 10-15 цифр.";
        }
        if (password == null || password.length() < 6) {
            return "❌ Ошибка: Пароль должен быть не менее 6 символов.";
        }
        return null;
    }

    public static String validateDish(String name, String description, double price) {
        if (name == null || name.trim().isEmpty()) {
            return "❌ Название блюда не может быть пустым.";
        }
        if (description == null || description.trim().isEmpty()) {
            return "❌ Описание блюда не может быть пустым.";
        }
        if (price <= 0) {
            return "❌ Цена блюда должна быть больше 0.";
        }
        return null;
    }

    public static String validateMenuInput(String name, List<String> dishIDs) {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            return "❌ Ошибка: Некорректное название меню!";
        }
        if (dishIDs == null || dishIDs.isEmpty()) {
            return "❌ Ошибка: Меню должно содержать хотя бы одно блюдо.";
        }
        return null;
    }
}

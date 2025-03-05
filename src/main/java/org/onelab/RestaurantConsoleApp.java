package org.onelab;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.OrderStatus;
import org.onelab.service.RestaurantService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class RestaurantConsoleApp {
    private final RestaurantService restaurantService;
    private final Scanner scanner;

    public RestaurantConsoleApp(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Показать пользователей");
            System.out.println("3. Добавить блюдо");
            System.out.println("4. Показать меню");
            System.out.println("5. Создать заказ");
            System.out.println("6. Показать заказы");
            System.out.println("7. Обновить статус заказа");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addUser();
                case 2 -> viewUsers();
                case 3 -> addDish();
                case 4 -> viewMenu();
                case 5 -> createOrder();
                case 6 -> viewOrders();
                case 7 -> updateOrderStatus();
                case 0 -> {
                    System.out.println("Выход...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }

    public String addUser() {
        System.out.print("Введите имя пользователя: ");
        String name = scanner.nextLine();

        System.out.print("Введите номер телефона: ");
        long phone = Long.parseLong(scanner.nextLine());

        UserDto user = UserDto.builder()
                .name(name)
                .phone(phone)
                .build();

        long userId = restaurantService.addUser(user);
        String result = "Пользователь добавлен ID: " + userId;
        System.out.println(result);
        return result;
    }

    public List<UserDto> viewUsers() {
        List<UserDto> users = restaurantService.getUsers();
        users.forEach(user -> System.out.println(user.getId() + ": " + user.getName() + " (" + user.getPhone() + ")"));
        return users;
    }

    public String addDish() {
        System.out.print("Введите название блюда: ");
        String name = scanner.nextLine();

        System.out.print("Введите цену блюда: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        DishDto dish = DishDto.builder()
                .name(name)
                .price(price)
                .build();

        long dishId = restaurantService.addDish(dish);
        String result = "Блюдо добавлено ID: " + dishId + " - " + name + " - " + price + "тг";
        System.out.println(result);
        return result;
    }

    public List<DishDto> viewMenu() {
        List<DishDto> dishes = restaurantService.getDishes();
        dishes.forEach(dish -> System.out.println(dish.getId() + ": " + dish.getName() + " - " + dish.getPrice() + "тг"));
        return dishes;
    }

    public String createOrder() {
        System.out.print("Введите ID пользователя: ");

        String userInput = scanner.nextLine().trim();
        if (!userInput.matches("\\d+")) {
            System.out.println("Ошибка: ID пользователя должен быть числом.");
            return "Ошибка ввода";
        }
        long userId = Long.parseLong(scanner.nextLine().trim());

        UserDto user = restaurantService.getUser(userId);
        if (user == null) {
            String error = "Пользователь не найден.";
            System.out.println(error);
            return error;
        }

        System.out.println("Введите ID блюд (0 для завершения):");
        List<DishDto> dishes = restaurantService.getDishes();
        dishes.forEach(dish -> System.out.println(dish.getId() + ": " + dish.getName() + " - " + dish.getPrice() + "тг"));

        List<DishDto> selectedDishes = new ArrayList<>();
        while (true) {
            String dishInput = scanner.nextLine().trim();
            if (!dishInput.matches("\\d+")) {
                System.out.println("Ошибка: ID блюда должен быть числом.");
                continue;
            }

            long dishId = Long.parseLong(dishInput);
            if (dishId == 0) break;

            dishes.stream().filter(d -> d.getId() == dishId).findFirst().ifPresent(selectedDishes::add);
        }

        OrderDto order = OrderDto.builder()
                .customer(user)
                .dishes(selectedDishes)
                .status(OrderStatus.NEW)
                .build();

        long orderId = restaurantService.addOrder(order);
        String result = "Заказ создан ID " + orderId;
        System.out.println(result);
        return result;
    }


    public List<OrderDto> viewOrders() {
        List<OrderDto> orders = restaurantService.getOrders();
        orders.forEach(order -> {
            System.out.println("Заказ #" + order.getId() + " для " + order.getCustomer().getName() +
                    " (Статус: " + order.getStatus() + ")");
            order.getDishes().forEach(dish -> System.out.println("  - " + dish.getName() + " (" + dish.getPrice() + "тг)"));
        });
        return orders;
    }

    public String updateOrderStatus() {
        System.out.print("Введите ID заказа: ");
        long orderId = scanner.nextLong();
        scanner.nextLine();

        OrderDto order = restaurantService.getOrder(orderId);
        if (order == null) {
            String error = "Заказ не найден.";
            System.out.println(error);
            return error;
        }

        System.out.println("Выберите новый статус заказа:");
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(status.ordinal() + 1 + ". " + status);
        }

        System.out.print("Введите номер статуса: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > OrderStatus.values().length) {
            String error = "Некорректный выбор статуса.";
            System.out.println("Некорректный выбор статуса.");
            return error;
        }

        OrderStatus newStatus = OrderStatus.values()[choice - 1];

        OrderDto updatedOrder = OrderDto.builder()
                .id(order.getId())
                .customer(order.getCustomer())
                .dishes(order.getDishes())
                .status(newStatus)
                .totalPrice(order.getTotalPrice())
                .build();

        restaurantService.updateOrder(updatedOrder);
        String result = "Статус заказа обновлён на " + newStatus + ".";
        System.out.println(result);
        return result;
    }

}

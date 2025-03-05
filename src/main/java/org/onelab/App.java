package org.onelab;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.OrderStatus;
import org.onelab.service.RestaurantService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

@SpringBootApplication
public class App {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        RestaurantService restaurantService = context.getBean(RestaurantService.class);

        while (true) {
            System.out.println("""
                    1. Добавить пользователя
                    2. Посмотреть пользователей
                    3. Добавить блюдо
                    4. Посмотреть меню
                    5. Создать заказ
                    6. Посмотреть заказы
                    7. Обновить статус заказа
                    8. Выход
                    """);

            switch (scanner.nextInt()) {
                case 1 -> addUser(restaurantService);
                case 2 -> viewUsers(restaurantService);
                case 3 -> addDish(restaurantService);
                case 4 -> viewMenu(restaurantService);
                case 5 -> createOrder(restaurantService);
                case 6 -> viewOrders(restaurantService);
                case 7 -> updateOrderStatus(restaurantService);
                case 8 -> System.exit(0);
                default -> System.out.println("Неверный ввод");
            }
        }
    }

    private static void addUser(RestaurantService restaurantService) {
        System.out.print("Имя: ");
        String name = scanner.next();
        System.out.print("Телефон: ");
        Long phone = scanner.nextLong();
        restaurantService.addUser(UserDto.builder()
                .name(name)
                .phone(phone)
                .build());
        System.out.println("Пользователь добавлен\n");
    }

    private static void viewUsers(RestaurantService restaurantService) {
        List<UserDto> users = restaurantService.getUsers();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей.");
        } else {
            users.forEach(user -> System.out.println(user.getId() + ": " + user.getName() + " (" + user.getPhone() + ")"));
        }
    }

    private static void addDish(RestaurantService restaurantService) {
        System.out.print("Название блюда: ");
        String name = scanner.next();
        System.out.print("Цена: ");
        double price = scanner.nextDouble();
        DishDto dish = DishDto.builder().name(name).price(price).build();
        restaurantService.addDish(dish);
        System.out.println("Блюдо добавлено: " + dish.getName() + " - " + dish.getPrice() + "тг\n");
    }

    private static void viewMenu(RestaurantService restaurantService) {
        List<DishDto> dishes = restaurantService.getDishes();
        if (dishes.isEmpty()) {
            System.out.println("Меню пусто.");
        } else {
            dishes.forEach(dish -> System.out.println(dish.getId() + ": " + dish.getName() + " - " + dish.getPrice() + "тг"));
        }
    }

    private static void createOrder(RestaurantService restaurantService) {
        System.out.print("ID пользователя: ");
        long userId = scanner.nextLong();
        UserDto user = restaurantService.getUser(userId);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }
        List<DishDto> selectedDishes = new ArrayList<>();
        while (true) {
            viewMenu(restaurantService);
            System.out.print("ID блюда (0 для завершения): ");
            long dishId = scanner.nextLong();
            if (dishId == 0) break;
            restaurantService.getDishes().stream()
                    .filter(d -> d.getId() == dishId)
                    .findFirst()
                    .ifPresent(selectedDishes::add);
        }
        if (selectedDishes.isEmpty()) {
            System.out.println("Заказ не создан (блюда не выбраны).\n");
            return;
        }

        restaurantService.addOrder(
                OrderDto.builder()
                        .customer(user)
                        .dishes(selectedDishes)
                        .status(OrderStatus.NEW)
                        .build());
        System.out.println("Заказ создан.\n");
    }

    private static void viewOrders(RestaurantService restaurantService) {
        List<OrderDto> orders = restaurantService.getOrders();
        if (orders.isEmpty()) {
            System.out.println("Нет заказов.");
        } else {
            orders.forEach(order -> {
                System.out.println("Заказ #" + order.getId() + " для " + order.getCustomer().getName() + " (Статус: " + order.getStatus() + ")");
                order.getDishes().forEach(dish -> System.out.println("  - " + dish.getName() + " (" + dish.getPrice() + "тг)"));
            });
        }
    }

    private static void updateOrderStatus(RestaurantService restaurantService) {
        System.out.print("Введите ID заказа: ");
        long orderId = scanner.nextLong();
        OrderDto order = restaurantService.getOrder(orderId);
        if (order == null) {
            System.out.println("Заказ не найден.");
            return;
        }
        System.out.println("Выберите новый статус: 1. Готовится 2. Отменен 3. Завершён");
        int statusChoice = scanner.nextInt();
        switch (statusChoice) {
            case 1 -> order.setStatus(OrderStatus.PROCESSING);
            case 2 -> order.setStatus(OrderStatus.CANCELLED);
            case 3 -> order.setStatus(OrderStatus.COMPLETED);
            default -> {
                System.out.println("Неверный выбор статуса.");
                return;
            }
        }
        restaurantService.updateOrder(order);
        System.out.println("Статус заказа обновлён.");
    }
}

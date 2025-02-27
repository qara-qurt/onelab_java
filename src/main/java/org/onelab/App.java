package org.onelab;

import org.onelab.config.AppConfig;
import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.OrderStatus;
import org.onelab.service.RestaurantService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    private static final RestaurantService restaurantService = context.getBean(RestaurantService.class);


    public static void main(String[] args) {
        while (true) {
            System.out.println("" +
                    "1. Добавить пользователя\n" +
                    "2. Посмотреть пользователей\n" +
                    "3. Добавить блюдо\n" +
                    "4. Посмотреть меню\n" +
                    "5. Создать заказ\n" +
                    "6. Посмотреть заказы\n" +
                    "7. Обновить статус заказа\n" +
                    "8. Выход");

            switch (scanner.nextInt()) {
                case 1 -> addUser();
                case 2 -> viewUsers();
                case 3 -> addDish();
                case 4 -> viewMenu();
                case 5 -> createOrder();
                case 6 -> viewOrders();
                case 7 -> updateOrderStatus();
                case 8 -> System.exit(0);
                default -> System.out.println("Неверный ввод");
            }
        }
    }

    private static void addUser() {
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

    private static void viewUsers() {
        List<UserDto> users = restaurantService.getUsers();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей.");
        } else {
            users.forEach(user -> System.out.println(user.getId() + ": " + user.getName() + " (" + user.getPhone() + ")"));
        }
    }

    private static void addDish() {
        System.out.print("Название блюда: ");
        String name = scanner.next();
        System.out.print("Цена: ");
        double price = scanner.nextDouble();
        DishDto dish = DishDto.builder().id(System.currentTimeMillis()).name(name).price(price).build();
        restaurantService.addDish(dish);
        System.out.println("Блюдо добавлено: " + dish.getName() + " - " + dish.getPrice() + "тг\n");
    }

    private static void viewMenu() {
        List<DishDto> dishes = restaurantService.getDishes();
        if (dishes.isEmpty()) {
            System.out.println("Меню пусто.");
        } else {
            dishes.forEach(dish -> System.out.println(dish.getId() + ": " + dish.getName() + " - " + dish.getPrice() + "тг"));
        }
    }

    private static void createOrder() {
        System.out.print("ID пользователя: ");
        long userId = scanner.nextLong();
        UserDto user = restaurantService.getUser(userId);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }
        List<DishDto> selectedDishes = new ArrayList<>();
        while (true) {
            viewMenu();
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
                        .id(System.currentTimeMillis())
                        .customer(user)
                        .dishes(selectedDishes)
                        .status(OrderStatus.NEW)
                        .build());
        System.out.println("Заказ создан.\n");
    }

    private static void viewOrders() {
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

    private static void updateOrderStatus() {
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

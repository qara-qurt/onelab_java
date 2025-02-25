package org.onelab;

import org.onelab.config.AppConfig;
import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.service.RestaurantService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

public class App {
    // Read input from console
    private static final Scanner scanner = new Scanner(System.in);
    // Make application context using AppConfig
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    // Get RestaurantService bean from context
    private static final RestaurantService restaurantService = context.getBean(RestaurantService.class);

    // Menu
    private static final List<DishDto> menu = new ArrayList<>(List.of(
            DishDto.builder().id(1L).name("Pasta").price(2300).build(),
            DishDto.builder().id(2L).name("Pelmeni").price(1800).build(),
            DishDto.builder().id(3L).name("Borsh").price(2000).build()
    ));


    public static void main(String[] args) {
        // Add menu to restaurant
        for(DishDto dish : menu){
            restaurantService.addDish(dish);
        }
        while (true) {
            System.out.println("1. Добавить пользователя\n2. Посмотреть пользователей\n3. Добавить блюдо\n4. Посмотреть меню\n5. Создать заказ\n6. Посмотреть заказы\n7. Выход");
            switch (scanner.nextInt()) {
                case 1 -> addUser();
                case 2 -> viewUsers();
                case 3 -> addDish();
                case 4 -> viewMenu();
                case 5 -> createOrder();
                case 6 -> viewOrders();
                case 7 -> System.exit(0);
                default -> System.out.println("Неверный ввод");
            }
        }
    }

    private static void addUser() {
        System.out.print("Имя: ");
        String name = scanner.next();
        System.out.print("Телефон: ");
        Long phone = scanner.nextLong();

        UserDto user = UserDto.builder()
                .id(System.currentTimeMillis())
                .name(name)
                .phone(phone)
                .build();

        restaurantService.addUser(user);
        System.out.println("Пользователь добавлен ID - " + user.getId() + "\n");
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
        int price = scanner.nextInt();

        DishDto dish = DishDto.builder()
                .id(System.currentTimeMillis())
                .name(name)
                .price(price)
                .build();

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
            System.out.println("Выберите блюдо:");
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
                        .build());

        System.out.println("Заказ создан.\n");
    }

    private static void viewOrders() {
        List<OrderDto> orders = restaurantService.getOrders();
        if (orders.isEmpty()) {
            System.out.println("Нет заказов.");
        } else {
            orders.forEach(order -> {
                System.out.println("Заказ #" + order.getId() + " для " + order.getCustomer().getName());
                order.getDishes().forEach(dish -> System.out.println("  - " + dish.getName() + " (" + dish.getPrice() + "тг)"));
            });
        }
    }
}

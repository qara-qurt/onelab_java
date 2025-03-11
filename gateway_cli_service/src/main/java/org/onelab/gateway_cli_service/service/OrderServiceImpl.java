package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Order;
import org.onelab.gateway_cli_service.entity.User;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.DishRepository;
import org.onelab.gateway_cli_service.repository.MenuRepository;
import org.onelab.gateway_cli_service.repository.OrderRepository;
import org.onelab.gateway_cli_service.repository.UserRepository;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    @Override
    public String createOrder(String customerId, List<String> dishIDs) {
        Optional<User> user = userRepository.findById(customerId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("❌ Пользователь с ID " + customerId + " не найден.");
        }

        List<Dish> foundDishes = (List<Dish>) dishRepository.findAllById(dishIDs);
        if (foundDishes.size() != dishIDs.size()) {
            List<String> missingDishes = dishIDs.stream()
                    .filter(id -> foundDishes.stream().noneMatch(dish -> dish.getId().equals(id)))
                    .toList();
            throw new IllegalArgumentException("❌ Некоторые блюда не найдены: " + missingDishes);
        }

        boolean dishesExistInMenus = StreamSupport.stream(menuRepository.findAll().spliterator(), false)
                .filter(menu -> menu.getDishes() != null)
                .flatMap(menu -> menu.getDishes().stream())
                .map(Dish::getId)
                .anyMatch(dishIDs::contains);

        if (!dishesExistInMenus) {
            throw new IllegalArgumentException("❌ Выбранные блюда не находятся ни в одном меню.");
        }

        kafkaProducer.createOrder(customerId, dishIDs);
        return "✅ Заказ для клиента " + customerId + " отправлен в обработку.";
    }

    public String getOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.map(Utils::formatOrder)
                .orElse("❌ Заказ с ID " + orderId + " не найден.");
    }

    public String getOrdersByUser(String userId, int page, int size) {
        Page<Order> orders = orderRepository.findByCustomerId(userId, PageRequest.of(page - 1, size));
        if (orders.isEmpty()) {
            return "❌ У клиента " + userId + " нет заказов.";
        }

        return orders.stream()
                .map(Utils::formatOrder)
                .collect(Collectors.joining("\n"));
    }

    public String getOrders(int page, int size) {
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page - 1, size));
        if (orders.isEmpty()) {
            return "❌ Заказы отсутствуют.";
        }

        return orders.stream()
                .map(Utils::formatOrder)
                .collect(Collectors.joining("\n"));
    }
}

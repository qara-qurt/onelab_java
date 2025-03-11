package org.onelab.restaurant_service.service;

import lombok.AllArgsConstructor;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.entity.Order;
import org.onelab.restaurant_service.entity.OrderStatus;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.MenuRepository;
import org.onelab.restaurant_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    @Override
    public String createOrder(String userId, List<String> dishIDs) {

        List<Dish> foundDishes = (List<Dish>) dishRepository.findAllById(dishIDs);
        if (foundDishes.size() != dishIDs.size()) {
            List<String> missingDishes = dishIDs.stream()
                    .filter(id -> foundDishes.stream().noneMatch(dish -> dish.getId().equals(id)))
                    .toList();
            throw new NotFoundException("Dishes not found: " + missingDishes);
        }

        boolean dishesExistInMenus = StreamSupport.stream(menuRepository.findAll().spliterator(), false)
                .filter(menu -> menu.getDishes() != null)
                .flatMap(menu -> menu.getDishes().stream())
                .map(Dish::getId)
                .anyMatch(dishIDs::contains);

        if (!dishesExistInMenus) {
            throw new NotFoundException("Chosen dishes are not in any menu.");
        }

        double totalPrice = foundDishes.stream().mapToDouble(Dish::getPrice).sum();


        Order order = Order.builder()
                .customerId(userId)
                .dishes(foundDishes)
                .status(OrderStatus.NEW)
                .totalPrice(totalPrice)
                .build();

        return orderRepository.save(order).getId();
    }
}

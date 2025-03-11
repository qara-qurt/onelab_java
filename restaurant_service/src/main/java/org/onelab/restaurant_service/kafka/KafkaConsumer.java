package org.onelab.restaurant_service.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.entity.Menu;
import org.onelab.restaurant_service.service.DishService;
import org.onelab.restaurant_service.service.MenuService;
import org.onelab.restaurant_service.service.OrderService;
import org.onelab.restaurant_service.utils.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final DishService dishService;
    private final MenuService menuService;
    private final OrderService orderService;

    // Add Dish
    @KafkaListener(
            topics = KafkaTopics.DISH_ADD,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void createDish(String dishJson) {
        try {
            Dish dish = objectMapper.readValue(dishJson, Dish.class);
            String id = dishService.save(dish);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

    // Remove Dish
    @KafkaListener(
            topics = KafkaTopics.DISH_REMOVE,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void removeDish(String dishID) {
        try {
            String id = objectMapper.readValue(dishID, String.class);
            dishService.remove(id);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

    // Add Menu
    @KafkaListener(
            topics = KafkaTopics.MENU_ADD,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void addMenu(String menuJson) {
        try {
            Menu menu = objectMapper.readValue(menuJson, Menu.class);
            String id = menuService.addMenu(menu);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

    // Remove Menu
    @KafkaListener(
            topics = KafkaTopics.MENU_REMOVE,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void removeMenu(String menuID) {
        try {
            String id = objectMapper.readValue(menuID, String.class);
            menuService.removeMenu(id);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

    // Add Dish to Menu
    @KafkaListener(
            topics = KafkaTopics.ADD_DISH_TO_MENU,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void addDishToMenu(ConsumerRecord<String,String> record) {
        String menuID = record.key();
        String jsonDishes = record.value();

        try {
            List<String> dishIDs = objectMapper.readValue(jsonDishes, new TypeReference<List<String>>() {});
            menuService.addDishesToMenu(menuID, dishIDs);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize error: " + e.getMessage());
        }
    }

    // Remove Dish from Menu
    @KafkaListener(
            topics = KafkaTopics.REMOVE_DISH_FROM_MENU,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void removeDishFromMenu(ConsumerRecord<String, String> record) {
        String menuID = record.key();
        String jsonDishes = record.value();

        try {
            List<String> dishIDs = objectMapper.readValue(jsonDishes, new TypeReference<List<String>>() {});

            menuService.removeDishesFromMenu(menuID, dishIDs);
        } catch (Exception e) {
            throw new RuntimeException("❌ Deserialize error: " + e.getMessage());
        }
    }

    // Create Order
    @KafkaListener(
            topics = KafkaTopics.CREATE_ORDER,
            groupId = KafkaTopics.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void createOrder(ConsumerRecord<String, String> record) {
        String userID = record.key();
        String jsonDishes = record.value();

        try {
            List<String> dishIDs = objectMapper.readValue(jsonDishes, new TypeReference<List<String>>() {});

            orderService.createOrder(userID, dishIDs);
        } catch (Exception e) {
            throw new RuntimeException("❌ Deserialize error: " + e.getMessage());
        }
    }

}

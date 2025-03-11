package org.onelab.gateway_cli_service.kafka;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Menu;
import org.onelab.gateway_cli_service.entity.User;
import org.onelab.gateway_cli_service.utils.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // User topics
    public void sendUser(User user) {
        kafkaTemplate.send(KafkaTopics.USER_CREATE, user.getUsername(), user);
    }

    public void fillBalance(String userId, double balance) {
        kafkaTemplate.send(KafkaTopics.USER_FILL_BALANCE, userId, balance);
    }
    // Dish topics
    public void addDish(Dish dish) {
        kafkaTemplate.send(KafkaTopics.DISH_ADD, dish.getName(), dish);
    }

    public void removeDish(String id) {
        kafkaTemplate.send(KafkaTopics.DISH_REMOVE, id);
    }

    // Menu topics
    public void addMenu(Menu menu) {
        kafkaTemplate.send(KafkaTopics.MENU_ADD,menu.getName(), menu);
    }

    public void removeMenu(String id) {
        kafkaTemplate.send(KafkaTopics.MENU_REMOVE, id, id);
    }

    public void addDishToMenu(Menu menu, List<String> dishes) {
        kafkaTemplate.send(KafkaTopics.ADD_DISH_TO_MENU, menu.getId(), dishes);
    }

    public void removeDishFromMenu(Menu menu, List<String> dishes) {
        kafkaTemplate.send(KafkaTopics.REMOVE_DISH_FROM_MENU, menu.getId(), dishes);
    }

    // Order topics
    public void createOrder(String userID,List<String> dishIDs) {
        kafkaTemplate.send(KafkaTopics.CREATE_ORDER, userID,dishIDs);
    }

    public void cancelOrder(String id) {
        kafkaTemplate.send(KafkaTopics.CANCEL_ORDER, id);
    }
}

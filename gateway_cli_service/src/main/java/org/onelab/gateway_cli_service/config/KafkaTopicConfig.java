package org.onelab.gateway_cli_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.onelab.gateway_cli_service.utils.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreate() {
        return new NewTopic(KafkaTopics.USER_CREATE, 1, (short) 1);
    }

    @Bean
    public NewTopic addDish() {
        return new NewTopic(KafkaTopics.DISH_ADD, 1, (short) 1);
    }

    @Bean
    public NewTopic removeDish() {
        return new NewTopic(KafkaTopics.DISH_REMOVE, 1, (short) 1);
    }

    @Bean
    public NewTopic addMenu() {
        return new NewTopic(KafkaTopics.MENU_ADD, 1, (short) 1);
    }

    @Bean
    public NewTopic removeMenu() {
        return new NewTopic(KafkaTopics.MENU_REMOVE, 1, (short) 1);
    }

    @Bean
    public NewTopic addDishToMenu() {
        return new NewTopic(KafkaTopics.ADD_DISH_TO_MENU, 1, (short) 1);
    }

    @Bean
    public NewTopic removeDishFromMenu() {
        return new NewTopic(KafkaTopics.REMOVE_DISH_FROM_MENU, 1, (short) 1);
    }

    @Bean
    public NewTopic createOrder() {
        return new NewTopic(KafkaTopics.CREATE_ORDER, 1, (short) 1);
    }

    @Bean
    public NewTopic cancelOrder() {
        return new NewTopic(KafkaTopics.CANCEL_ORDER, 1, (short) 1);
    }

    @Bean
    public NewTopic fillBalance() {
        return new NewTopic(KafkaTopics.USER_FILL_BALANCE, 1, (short) 1);
    }
}

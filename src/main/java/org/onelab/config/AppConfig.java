package org.onelab.config;

import org.onelab.repository.*;
import org.onelab.service.RestaurantService;;
import org.onelab.service.RestaurantServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackages = "org.onelab")
public class AppConfig {

    @Bean
    @Primary
    public DishRepository dishRepository() {
        return new DishRepositoryImpl();
    }

    @Bean
    @Primary
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }

    @Bean
    @Primary
    public OrderRepository orderRepository() {
        return new OrderRepositoryImpl();
    }

    @Bean
    @Primary
    public RestaurantService restaurantService(
            DishRepository dishRepository,
            UserRepository userRepository,
            OrderRepository orderRepository) {
        return new RestaurantServiceImpl(userRepository, dishRepository, orderRepository) {
        };
    }
}

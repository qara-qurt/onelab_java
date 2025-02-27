package org.onelab.config;

import org.onelab.repository.*;
import org.onelab.service.RestaurantService;;
import org.onelab.service.RestaurantServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan(basePackages = "org.onelab")
public class AppConfig {


    @Bean
    @Primary
    public DishRepository dishRepository(JdbcTemplate jdbcTemplate) {
        return new DishRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @Primary
    public UserRepository userRepository(JdbcTemplate jdbcTemplate) {
        return new UserRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @Primary
    public OrderRepository orderRepository(JdbcTemplate jdbcTemplate) {
        return new OrderRepositoryImpl(jdbcTemplate);
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

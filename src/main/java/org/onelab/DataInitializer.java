package org.onelab;

import org.onelab.entity.Dish;
import org.onelab.entity.User;
import org.onelab.repository.DishRepository;
import org.onelab.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner initData(UserRepository userRepository, DishRepository dishRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.saveAll(List.of(
                        User.builder().name("Самат").phone(Long.valueOf("87778657787")).build(),
                        User.builder().name("Алихан").phone(Long.valueOf("87778657786")).build()
                ));
            }

            if (dishRepository.count() == 0) {
                dishRepository.saveAll(List.of(
                        Dish.builder().name("Пицца Маргарите").price(800.0).build(),
                        Dish.builder().name("Паста").price(1400.0).build(),
                        Dish.builder().name("Рамен").price(1200.0).build(),
                        Dish.builder().name("Куырдак").price(2900.0).build(),
                        Dish.builder().name("Беш").price(5000.0).build()
                ));
            }
        };
    }
}

package org.onelab.restaurant_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    @Test
    void save_WhenDishDoesNotExist_ShouldReturnDishId() {
        Dish dish = Dish.builder().name("Pasta").price(1200.0).build();

        when(dishRepository.findByName("Pasta")).thenReturn(Optional.empty());
        when(dishRepository.save(dish)).thenReturn(Dish.builder().id("123").name("Pasta").price(1200.0).build());

        String dishId = dishService.save(dish);

        assertThat(dishId).isEqualTo("123");
        verify(dishRepository).save(dish);
    }

    @Test
    void save_WhenDishExists_ShouldThrowAlreadyExistException() {
        Dish dish = Dish.builder().name("Pizza").price(1500.0).build();

        when(dishRepository.findByName("Pizza")).thenReturn(Optional.of(dish));

        assertThatThrownBy(() -> dishService.save(dish))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Dish with name Pizza already exist.");

        verify(dishRepository, never()).save(any());
    }

    @Test
    void remove_WhenDishExists_ShouldRemoveDish() {
        Dish dish = Dish.builder().id("456").name("Salad").price(800.0).build();

        when(dishRepository.findById("456")).thenReturn(Optional.of(dish));

        dishService.remove("456");

        verify(dishRepository).delete(dish);
    }

    @Test
    void remove_WhenDishNotExists_ShouldThrowNotFoundException() {
        when(dishRepository.findById("789")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dishService.remove("789"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Dish with id 789 not found");

        verify(dishRepository, never()).delete(any());
    }

    @Test
    void save_WhenRepositoryThrowsException_ShouldPropagateException() {
        Dish dish = Dish.builder().name("Burger").price(1300.0).build();

        when(dishRepository.findByName("Burger")).thenReturn(Optional.empty());
        when(dishRepository.save(dish)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> dishService.save(dish))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(dishRepository).save(dish);
    }

    @Test
    void remove_WhenDishDeletionFails_ShouldThrowRuntimeException() {
        Dish dish = Dish.builder().id("999").name("Soup").price(500.0).build();

        when(dishRepository.findById("456")).thenReturn(Optional.of(dish));
        doThrow(new RuntimeException("Deletion error")).when(dishRepository).delete(dish);

        assertThatThrownBy(() -> dishService.remove("456"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Deletion error");

        verify(dishRepository).delete(dish);
    }
}
package org.onelab.restaurant_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.entity.Dish;
import org.onelab.restaurant_service.entity.Menu;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void addMenu_WhenMenuDoesNotExist_ShouldReturnMenuId() {
        Menu menu = Menu.builder().name("Lunch").build();

        when(menuRepository.findByName("Lunch")).thenReturn(Optional.empty());
        when(menuRepository.save(menu)).thenReturn(Menu.builder().id("123").name("Lunch").build());

        String menuId = menuService.addMenu(menu);

        assertThat(menuId).isEqualTo("123");
        verify(menuRepository).save(menu);
    }

    @Test
    void addMenu_WhenMenuExists_ShouldThrowAlreadyExistException() {
        Menu menu = Menu.builder().name("Dinner").build();

        when(menuRepository.findByName("Dinner")).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.addMenu(menu))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Menu with this name Dinner already exist");

        verify(menuRepository, never()).save(any());
    }

    @Test
    void removeMenu_WhenMenuExists_ShouldDeleteMenu() {
        Menu menu = Menu.builder().id("menu123").build();

        when(menuRepository.findById("menu123")).thenReturn(Optional.of(menu));

        menuService.removeMenu("menu123");

        verify(menuRepository).deleteById("menu123");
    }

    @Test
    void removeMenu_WhenMenuNotExists_ShouldThrowNotFoundException() {
        when(menuRepository.findById("menu789")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.removeMenu("menu789"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu with ID 'menu789' not found.");

        verify(menuRepository, never()).deleteById(any());
    }

    @Test
    void addDishesToMenu_WhenMenuExistsAndDishesValid_ShouldUpdateMenu() {
        Menu menu = Menu.builder().id("menu456").dishes(new ArrayList<>()).build();
        Dish dish1 = Dish.builder().id("dish1").build();
        Dish dish2 = Dish.builder().id("dish2").build();

        when(menuRepository.findById("menu456")).thenReturn(Optional.of(menu));
        when(dishRepository.findAllById(List.of("dish1", "dish2"))).thenReturn(List.of(dish1, dish2));

        menuService.addDishesToMenu("menu456", List.of("dish1", "dish2"));

        assertThat(menu.getDishes()).containsExactlyInAnyOrder(dish1, dish2);
        verify(menuRepository).save(menu);
    }

    @Test
    void removeDishesFromMenu_WhenMenuAndDishesExist_ShouldRemoveDishes() {
        Dish dish1 = Dish.builder().id("dish1").build();
        Dish dish2 = Dish.builder().id("dish2").build();

        Menu menu = Menu.builder().id("menu456").dishes(new ArrayList<>(List.of(dish1, dish2))).build();

        when(menuRepository.findById("menu456")).thenReturn(Optional.of(menu));

        menuService.removeDishesFromMenu("menu456", List.of("dish1"));

        assertThat(menu.getDishes()).containsExactly(dish2);
        verify(menuRepository).save(menu);
    }

    @Test
    void removeDishesFromMenu_WhenNoDishesRemoved_ShouldThrowIllegalArgumentException() {
        Dish dish1 = Dish.builder().id("dish1").build();

        Menu menu = Menu.builder().id("menu789").dishes(new ArrayList<>(List.of(dish1))).build();

        when(menuRepository.findById("menu789")).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.removeDishesFromMenu("menu789", List.of("dish999")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No dishes were removed");

        verify(menuRepository, never()).save(any());
    }

    @Test
    void removeDishesFromMenu_WhenMenuEmpty_ShouldThrowIllegalArgumentException() {
        Menu menu = Menu.builder().id("menuEmpty").dishes(new ArrayList<>()).build();

        when(menuRepository.findById("menuEmpty")).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.removeDishesFromMenu("menuEmpty", List.of("dish1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Menu is empty.");

        verify(menuRepository, never()).save(any());
    }
}
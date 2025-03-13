package org.onelab.restaurant_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.dto.MenuDto;
import org.onelab.restaurant_service.dto.MenuRequestDto;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.entity.MenuEntity;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.MenuElasticRepository;
import org.onelab.restaurant_service.repository.MenuRepository;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.service.MenuServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    @Mock
    private MenuElasticRepository menuElasticRepository;

    private MenuEntity mockMenu;
    private DishEntity dish1;
    private DishEntity dish2;

    @BeforeEach
    void setUp() {
        dish1 = DishEntity.builder().id(1L).name("Pasta").build();
        dish2 = DishEntity.builder().id(2L).name("Pizza").build();

        mockMenu = MenuEntity.builder()
                .id(1L)
                .name("Lunch Special")
                .dishes(new ArrayList<>(List.of(dish1)))
                .build();
    }

    @Test
    void getMenu_ShouldReturnMenuDto_WhenMenuExists() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(mockMenu));

        MenuDto result = menuService.getMenu(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Lunch Special");

        verify(menuRepository, times(1)).findById(1L);
    }

    @Test
    void getMenu_ShouldThrowNotFoundException_WhenMenuDoesNotExist() {
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenu(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu not found.");

        verify(menuRepository, times(1)).findById(999L);
    }

    @Test
    void getMenus_ShouldReturnListOfMenuDtos() {
        List<MenuEntity> menus = List.of(mockMenu);
        Page<MenuEntity> page = new PageImpl<>(menus);

        when(menuRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        List<MenuDto> result = menuService.getMenus(1, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Lunch Special");

        verify(menuRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void createMenu_ShouldReturnMenuId_WhenMenuDoesNotExist() {
        MenuRequestDto menuRequestDto = new MenuRequestDto("Lunch Menu", List.of(1L, 2L));

        when(menuRepository.existsByName(anyString())).thenReturn(false);
        when(dishRepository.findAllById(anyList())).thenReturn(List.of(dish1, dish2));
        when(menuRepository.save(any(MenuEntity.class))).thenReturn(mockMenu);

        String menuId = menuService.createMenu(menuRequestDto);

        assertThat(menuId).isEqualTo("1");
        verify(menuRepository).save(any(MenuEntity.class));
    }

    @Test
    void createMenu_ShouldThrowAlreadyExistException_WhenMenuAlreadyExists() {
        MenuRequestDto menuRequestDto = new MenuRequestDto("Lunch Menu", List.of(1L, 2L));

        when(menuRepository.existsByName("Lunch Menu")).thenReturn(true);

        assertThatThrownBy(() -> menuService.createMenu(menuRequestDto))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Menu with this name already exists.");
    }

    @Test
    void createMenu_ShouldThrowNotFoundException_WhenNoValidDishesFound() {
        MenuRequestDto menuRequestDto = new MenuRequestDto("Lunch Menu", List.of(999L));

        when(menuRepository.existsByName("Lunch Menu")).thenReturn(false);
        when(dishRepository.findAllById(anyList())).thenReturn(List.of());

        assertThatThrownBy(() -> menuService.createMenu(menuRequestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No valid dishes found.");
    }

    @Test
    void removeMenu_ShouldDeleteMenu_WhenMenuExists() {
        when(menuRepository.existsById(1L)).thenReturn(true);

        menuService.removeMenu(1L);

        verify(menuRepository).deleteById(1L);
        verify(menuElasticRepository).deleteById("1"); // Ensures sync with Elasticsearch
    }

    @Test
    void removeMenu_ShouldThrowNotFoundException_WhenMenuNotExists() {
        when(menuRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.removeMenu(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu not found.");
    }

    @Test
    void addDishesToMenu_ShouldAddDishes_WhenMenuExists() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(mockMenu));
        when(dishRepository.findAllById(anyList())).thenReturn(List.of(dish2));

        menuService.addDishesToMenu(1L, List.of(2L));

        verify(menuRepository).save(any(MenuEntity.class));
    }

    @Test
    void addDishesToMenu_ShouldThrowNotFoundException_WhenMenuNotExists() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.addDishesToMenu(1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu not found.");
    }

    @Test
    void removeDishesFromMenu_ShouldRemoveDishes_WhenMenuExists() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(mockMenu));

        menuService.removeDishesFromMenu(1L, List.of(1L));

        verify(menuRepository).save(any(MenuEntity.class));
    }

    @Test
    void removeDishesFromMenu_ShouldThrowNotFoundException_WhenMenuNotExists() {
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.removeDishesFromMenu(1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Menu not found.");
    }
}

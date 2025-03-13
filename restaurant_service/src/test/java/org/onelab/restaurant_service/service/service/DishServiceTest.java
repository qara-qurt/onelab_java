package org.onelab.restaurant_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.restaurant_service.dto.DishDto;
import org.onelab.restaurant_service.entity.DishDocument;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.exception.AlreadyExistException;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.repository.DishElasticRepository;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.service.DishServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishElasticRepository dishElasticRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    private DishDto dishDto;
    private DishEntity dishEntity;

    @BeforeEach
    void setUp() {
        dishDto = DishDto.builder()
                .id(1L)
                .name("Pasta")
                .price(1200.0)
                .description("Delicious Italian pasta")
                .build();

        dishEntity = DishEntity.builder()
                .id(1L)
                .name("Pasta")
                .price(1200.0)
                .description("Delicious Italian pasta")
                .build();
    }

    @Test
    void save_WhenDishDoesNotExist_ShouldReturnDishId() {
        when(dishRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(dishRepository.save(any(DishEntity.class))).thenReturn(dishEntity);

        Long dishId = dishService.save(dishDto);

        assertThat(dishId).isEqualTo(1L);
        verify(dishRepository).save(any(DishEntity.class));
    }

    @Test
    void save_WhenDishExists_ShouldThrowAlreadyExistException() {
        when(dishRepository.findByName("Pasta")).thenReturn(Optional.of(dishEntity));

        assertThatThrownBy(() -> dishService.save(dishDto))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Dish with this name already exists");

        verify(dishRepository, never()).save(any());
    }

    @Test
    void remove_WhenDishExists_ShouldRemoveDish() {
        when(dishRepository.existsById(1L)).thenReturn(true);
        doNothing().when(dishRepository).deleteById(1L);

        dishService.remove(1L);

        verify(dishRepository).deleteById(1L);
    }

    @Test
    void remove_WhenDishNotExists_ShouldThrowNotFoundException() {
        when(dishRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> dishService.remove(2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Dish not found");

        verify(dishRepository, never()).deleteById(anyLong());
    }

    @Test
    void getDishById_WhenExists_ShouldReturnDishDto() {
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dishEntity));

        DishDto result = dishService.getDishById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pasta");

        verify(dishRepository).findById(1L);
    }

    @Test
    void getDishById_WhenNotExists_ShouldThrowNotFoundException() {
        when(dishRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dishService.getDishById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Dish not found");

        verify(dishRepository).findById(1L);
    }

    @Test
    void getDishes_ShouldReturnPagedDishes() {
        List<DishEntity> dishes = List.of(dishEntity);
        Page<DishEntity> page = new PageImpl<>(dishes);

        when(dishRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<DishDto> result = dishService.getDishes(1, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pasta");

        verify(dishRepository).findAll(any(PageRequest.class));
    }

    @Test
    void searchDishes_ShouldReturnMatchedDishes() {
        List<DishDocument> dishes = List.of(
                DishDocument.builder()
                        .id("1")
                        .name("Pasta")
                        .price(1200.0)
                        .description("Delicious Italian pasta")
                        .build()
        );

        Page<DishDocument> page = new PageImpl<>(dishes);

        when(dishElasticRepository.searchByNameOrDescription(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        List<DishDto> result = dishService.searchDishes("Pasta", 1, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pasta");

        verify(dishElasticRepository).searchByNameOrDescription(anyString(), any(PageRequest.class));
    }

}

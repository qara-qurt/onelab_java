package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DishServiceTest {

    private RestaurantClient restaurantClient;
    private DishServiceImpl dishService;

    @BeforeEach
    public void setup() {
        restaurantClient = mock(RestaurantClient.class);
        dishService = new DishServiceImpl(restaurantClient);
    }

    @Test
    public void testGetDishes_whenDishesExist_returnsFormattedDishes() {
        List<DishDto> mockDishes = List.of(
                DishDto.builder().id(1L).name("Pizza").description("Cheese").price(9.99).build(),
                DishDto.builder().id(2L).name("Burger").description("Beef").price(7.99).build()
        );

        when(restaurantClient.getDishes(1, 10)).thenReturn(mockDishes);

        String result = dishService.getDishes(1, 10);

        String expected = String.join("\n",
                Utils.formatDish(mockDishes.get(0)),
                Utils.formatDish(mockDishes.get(1))
        );

        assertEquals(expected, result);
    }


    @Test
    public void testGetDishes_whenNoDishes_returnsNotFoundMessage() {
        when(restaurantClient.getDishes(1, 10)).thenReturn(Collections.emptyList());

        String result = dishService.getDishes(1, 10);

        assertEquals("❌ Нет доступных блюд", result);
    }

    @Test
    public void testAddDish_whenSuccess_returnsSuccessMessage() {
        DishDto dish = DishDto.builder().name("Salad").description("Fresh").price(5.5).build();
        Map<String, Long> response = Map.of("id", 100L);

        when(restaurantClient.addDish(any())).thenReturn(response);

        String result = dishService.addDish("Salad", "Fresh", 5.5);

        assertEquals("✅ Блюдо добавлено! ID: 100", result);
    }

    @Test
    public void testAddDish_whenConflict_returnsErrorMessage() {
        DishDto dish = DishDto.builder().name("Salad").description("Fresh").price(5.5).build();

        when(restaurantClient.addDish(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        String result = dishService.addDish("Salad", "Fresh", 5.5);

        assertEquals("❌ Блюдо с таким названием уже существует.", result);
    }

    @Test
    public void testRemoveDish_whenSuccess_returnsSuccessMessage() {
        Long id = 42L;

        String result = dishService.removeDish(id);

        verify(restaurantClient).removeDish(id);
        assertEquals("✅ Блюдо с ID 42 удалено.", result);
    }

    @Test
    public void testRemoveDish_whenNotFound_returnsErrorMessage() {
        Long id = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(restaurantClient).removeDish(id);

        String result = dishService.removeDish(id);

        assertEquals("❌ Блюдо с ID 999 не найдено.", result);
    }

    @Test
    public void testSearchDishes_whenFound_returnsFormattedDishes() {
        List<DishDto> mockDishes = List.of(
                DishDto.builder().id(3L).name("Sushi").description("Fish").price(12.0).build()
        );

        when(restaurantClient.searchDishes("Sushi", 1, 10)).thenReturn(mockDishes);

        String result = dishService.searchDishes("Sushi", 1, 10);

        String expected = Utils.formatDish(mockDishes.get(0));

        assertEquals(expected, result);
    }


    @Test
    public void testSearchDishes_whenNotFound_returnsErrorMessage() {
        when(restaurantClient.searchDishes("NotExist", 1, 10)).thenReturn(Collections.emptyList());

        String result = dishService.searchDishes("NotExist", 1, 10);

        assertEquals("❌ Блюдо с параметром 'NotExist' не найдено.", result);
    }
}

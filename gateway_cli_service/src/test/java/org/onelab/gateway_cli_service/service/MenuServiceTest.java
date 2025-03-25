package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.common_lib.dto.MenuDto;
import org.onelab.common_lib.dto.MenuRequestDto;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.utils.Utils;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MenuServiceTest {

    private RestaurantClient restaurantClient;
    private MenuServiceImpl menuService;

    @BeforeEach
    void setUp() {
        restaurantClient = mock(RestaurantClient.class);
        menuService = new MenuServiceImpl(restaurantClient);
    }

    @Test
    void testGetMenu_whenFound_returnsFormattedMenu() {
        MenuDto menu = MenuDto.builder()
                .id(1L)
                .name("Lunch Menu")
                .dishes(List.of(
                        DishDto.builder().id(1L).name("Pizza").description("Cheese").price(9.99).build()
                ))
                .build();

        when(restaurantClient.getMenu(1L)).thenReturn(menu);

        String result = menuService.getMenu(1L);
        String expected = Utils.formatMenu(menu);

        assertEquals(expected, result);
    }

    @Test
    void testGetMenu_whenNotFound_returnsErrorMessage() {
        when(restaurantClient.getMenu(999L)).thenThrow(new RuntimeException("Not found"));

        String result = menuService.getMenu(999L);

        assertEquals("❌ Меню с ID '999' не найдено.", result);
    }

    @Test
    void testCreateMenu_whenValid_returnsSuccessMessage() {
        String menuName = "Special Menu";
        List<Long> dishIds = List.of(1L, 2L);

        when(restaurantClient.addMenu(any(MenuRequestDto.class))).thenReturn("42");

        String result = menuService.createMenu(menuName, dishIds);

        assertEquals("✅ Меню 'Special Menu' создано. ID: 42", result);
    }

    @Test
    void testCreateMenu_whenInvalid_returnsValidationMessage() {
        String result = menuService.createMenu("", Collections.emptyList());

        assertEquals("❌ Ошибка: Некорректное название меню!\n❌ Меню должно содержать хотя бы одно блюдо.\n", result);
    }

    @Test
    void testCreateMenu_whenExceptionThrown_returnsErrorMessage() {
        String name = "Dinner";
        List<Long> ids = List.of(1L);

        when(restaurantClient.addMenu(any(MenuRequestDto.class)))
                .thenThrow(new RuntimeException("Ошибка создания"));

        String result = menuService.createMenu(name, ids);

        assertEquals("❌ Ошибка при создании меню: Ошибка создания", result);
    }

    @Test
    void testRemoveMenu_whenSuccess_returnsSuccessMessage() {
        String result = menuService.removeMenu(10L);

        verify(restaurantClient).removeMenu(10L);
        assertEquals("🗑️ Меню с ID '10' удалено.", result);
    }

    @Test
    void testRemoveMenu_whenException_returnsErrorMessage() {
        doThrow(new RuntimeException("Not found")).when(restaurantClient).removeMenu(20L);

        String result = menuService.removeMenu(20L);

        assertEquals("❌ Ошибка при удалении меню: Not found", result);
    }

    @Test
    void testGetMenus_whenNotEmpty_returnsFormattedList() {
        MenuDto menu = MenuDto.builder()
                .id(1L)
                .name("Menu 1")
                .dishes(List.of(
                        DishDto.builder().id(1L).name("Soup").description("Hot").price(3.0).build()
                ))
                .build();

        when(restaurantClient.getMenus(1, 10)).thenReturn(List.of(menu));

        String result = menuService.getMenus(1, 10);

        assertEquals(Utils.formatMenu(menu), result);
    }

    @Test
    void testGetMenus_whenEmpty_returnsEmptyMessage() {
        when(restaurantClient.getMenus(1, 10)).thenReturn(Collections.emptyList());

        String result = menuService.getMenus(1, 10);

        assertEquals("📭 Меню отсутствуют.", result);
    }

    @Test
    void testAddDishToMenu_whenSuccess_returnsSuccessMessage() {
        String result = menuService.addDishToMenu(1L, List.of(1L, 2L));

        verify(restaurantClient).addDishesToMenu(1L, List.of(1L, 2L));
        assertEquals("✅ Добавлены блюда в меню: 1", result);
    }

    @Test
    void testAddDishToMenu_whenException_returnsErrorMessage() {
        doThrow(new RuntimeException("Ошибка")).when(restaurantClient).addDishesToMenu(any(), any());

        String result = menuService.addDishToMenu(1L, List.of(1L));

        assertEquals("❌ Ошибка при добавлении блюд в меню: Ошибка", result);
    }

    @Test
    void testRemoveDishFromMenu_whenSuccess_returnsSuccessMessage() {
        String result = menuService.removeDishFromMenu(2L, List.of(3L));

        verify(restaurantClient).removeDishesFromMenu(2L, List.of(3L));
        assertEquals("🗑️ Удалены блюда из меню: 2", result);
    }

    @Test
    void testRemoveDishFromMenu_whenException_returnsErrorMessage() {
        doThrow(new RuntimeException("Ошибка удаления")).when(restaurantClient).removeDishesFromMenu(any(), any());

        String result = menuService.removeDishFromMenu(2L, List.of(5L));

        assertEquals("❌ Ошибка при удалении блюд из меню: Ошибка удаления", result);
    }
}

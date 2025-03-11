package org.onelab.gateway_cli_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.gateway_cli_service.entity.Dish;
import org.onelab.gateway_cli_service.entity.Menu;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.repository.DishRepository;
import org.onelab.gateway_cli_service.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Menu testMenu;
    private Dish testDish;

    @BeforeEach
    void setUp() {
        testDish = Dish.builder()
                .id("dish-1")
                .name("Pizza")
                .description("Cheese Pizza")
                .price(12.99)
                .build();

        testMenu = Menu.builder()
                .id("menu-1")
                .name("Italian Menu")
                .dishes(List.of(testDish))
                .build();
    }

    @Test
    void shouldReturnMenu_WhenMenuExists() {
        when(menuRepository.findById("menu-1")).thenReturn(Optional.of(testMenu));

        String result = menuService.getMenu("menu-1");

        assertTrue(result.contains("Italian Menu"));
        assertTrue(result.contains("Pizza"));
        verify(menuRepository, times(1)).findById("menu-1");
    }

    @Test
    void shouldReturnErrorMessage_WhenMenuDoesNotExist() {
        when(menuRepository.findById("menu-999")).thenReturn(Optional.empty());

        String result = menuService.getMenu("menu-999");

        assertEquals("❌ Меню с ID 'menu-999' не найдено.", result);
    }

    @Test
    void shouldCreateMenu_WhenMenuDoesNotExist() {
        when(menuRepository.findByName("Italian Menu")).thenReturn(Optional.empty());
        when(dishRepository.findById("dish-1")).thenReturn(Optional.of(testDish));

        String result = menuService.createMenu("Italian Menu", List.of("dish-1"));

        assertTrue(result.contains("Создание меню: Italian Menu"));
        verify(kafkaProducer, times(1)).addMenu(any(Menu.class));
    }

    @Test
    void shouldThrowError_WhenMenuAlreadyExists() {
        when(menuRepository.findByName("Italian Menu")).thenReturn(Optional.of(testMenu));

        String result = menuService.createMenu("Italian Menu", List.of("dish-1"));

        assertEquals("❌ Меню с именем 'Italian Menu' уже существует.", result);
    }

    @Test
    void shouldRemoveMenu_WhenMenuExists() {
        when(menuRepository.findById("menu-1")).thenReturn(Optional.of(testMenu));

        String result = menuService.removeMenu("menu-1");

        assertTrue(result.contains("Удаление меню с ID: menu-1"));
        verify(kafkaProducer, times(1)).removeMenu("menu-1");
    }

    @Test
    void shouldThrowError_WhenRemovingNonExistentMenu() {
        when(menuRepository.findById("menu-999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                menuService.removeMenu("menu-999"));

        assertEquals("❌ Меню с ID 'menu-999' не найдено.", exception.getMessage());
    }

    @Test
    void shouldReturnMenus_WhenMenusExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Menu> menus = List.of(testMenu);
        Page<Menu> menuPage = new PageImpl<>(menus);

        when(menuRepository.findAll(pageRequest)).thenReturn(menuPage);

        String result = menuService.getMenus(1, 10);

        assertTrue(result.contains("Italian Menu"));
    }

    @Test
    void shouldReturnEmptyMessage_WhenNoMenusExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Menu> emptyPage = Page.empty();

        when(menuRepository.findAll(pageRequest)).thenReturn(emptyPage);

        String result = menuService.getMenus(1, 10);

        assertEquals("📭 Меню отсутствуют.", result);
    }

    @Test
    void shouldAddDishToMenu_WhenMenuExists() {
        Menu emptyMenu = Menu.builder()
                .id("menu-1")
                .name("Italian Menu")
                .dishes(List.of())
                .build();

        when(menuRepository.findById("menu-1")).thenReturn(Optional.of(emptyMenu));
        when(dishRepository.findAllById(List.of("dish-1"))).thenReturn(List.of(testDish));

        String result = menuService.addDishToMenu("menu-1", List.of("dish-1"));

        assertTrue(result.contains("Добавление блюд в меню: menu-1"));
        verify(kafkaProducer, times(1)).addDishToMenu(any(Menu.class), anyList());
    }


    @Test
    void shouldThrowError_WhenAddingDishToNonExistentMenu() {
        when(menuRepository.findById("menu-999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                menuService.addDishToMenu("menu-999", List.of("dish-1")));

        assertEquals("❌ Меню с ID 'menu-999' не найдено.", exception.getMessage());
    }

    @Test
    void shouldRemoveDishFromMenu_WhenMenuExists() {
        when(menuRepository.findById("menu-1")).thenReturn(Optional.of(testMenu));

        String result = menuService.removeDishFromMenu("menu-1", List.of("dish-1"));

        assertTrue(result.contains("Удаление блюд из меню: menu-1"));
        verify(kafkaProducer, times(1)).removeDishFromMenu(any(Menu.class), anyList());
    }

    @Test
    void shouldThrowError_WhenRemovingDishFromNonExistentMenu() {
        when(menuRepository.findById("menu-999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                menuService.removeDishFromMenu("menu-999", List.of("dish-1")));

        assertEquals("❌ Меню с ID 'menu-999' не найдено.", exception.getMessage());
    }
}

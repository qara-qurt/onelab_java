//package org.onelab.gateway_cli_service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onelab.gateway_cli_service.entity.Dish;
//import org.onelab.gateway_cli_service.kafka.KafkaProducer;
//import org.onelab.gateway_cli_service.repository.DishRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DishServiceTest {
//
//    @Mock
//    private DishRepository dishRepository;
//
//    @Mock
//    private KafkaProducer kafkaProducer;
//
//    @InjectMocks
//    private DishServiceImpl dishService;
//
//    private Dish testDish;
//
//    @BeforeEach
//    void setUp() {
//        testDish = Dish.builder()
//                .id("1")
//                .name("Pizza")
//                .description("Cheese Pizza")
//                .price(10.99)
//                .build();
//    }
//
//    @Test
//    void shouldReturnDishes_WhenDishesExist() {
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        List<Dish> dishes = List.of(testDish);
//        Page<Dish> page = new PageImpl<>(dishes);
//
//        when(dishRepository.findAll(pageRequest)).thenReturn(page);
//
//        String result = dishService.getDishes(1, 10);
//
//        assertTrue(result.contains("Pizza"));
//        verify(dishRepository, times(1)).findAll(pageRequest);
//    }
//
//    @Test
//    void shouldReturnNoDishesMessage_WhenNoDishesExist() {
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        Page<Dish> emptyPage = Page.empty();
//
//        when(dishRepository.findAll(pageRequest)).thenReturn(emptyPage);
//
//        String result = dishService.getDishes(1, 10);
//
//        assertEquals("❌ Нету блюд", result);
//    }
//
//    @Test
//    void shouldAddDish_WhenDishDoesNotExist() {
//        when(dishRepository.findByName("Pizza")).thenReturn(Optional.empty());
//
//        String result = dishService.addDish("Pizza", "Cheese Pizza", 10.99);
//
//        assertTrue(result.contains("✅ Блюдо \"Pizza\""));
//        verify(kafkaProducer, times(1)).addDish(any(Dish.class));
//    }
//
//    @Test
//    void shouldNotAddDish_WhenDishAlreadyExists() {
//        when(dishRepository.findByName("Pizza")).thenReturn(Optional.of(testDish));
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//                dishService.addDish("Pizza", "Cheese Pizza", 10.99));
//
//        assertEquals("❌ Блюдо с названием Pizza уже существует.", exception.getMessage());
//    }
//
//    @Test
//    void shouldRemoveDish_WhenDishExists() {
//        when(dishRepository.findById("1")).thenReturn(Optional.of(testDish));
//
//        String result = dishService.removeDish("1");
//
//        assertTrue(result.contains("✅ Блюдо с ID 1 будет удалено."));
//        verify(kafkaProducer, times(1)).removeDish("1");
//    }
//
//    @Test
//    void shouldNotRemoveDish_WhenDishDoesNotExist() {
//        when(dishRepository.findById("99")).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//                dishService.removeDish("99"));
//
//        assertEquals("❌ Блюдо с ID 99 нету.", exception.getMessage());
//    }
//
//    @Test
//    void shouldReturnDishes_WhenSearchHasResults() {
//        // Given
//        String searchName = "Pizza";
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Dish> dishPage = new PageImpl<>(List.of(testDish));
//
//        when(dishRepository.searchByFields(searchName, pageable)).thenReturn(dishPage);
//
//        String result = dishService.searchDishes(searchName, 1, 10);
//
//        assertTrue(result.contains("Pizza"));
//        verify(dishRepository, times(1)).searchByFields(searchName, pageable);
//    }
//
//    @Test
//    void shouldReturnErrorMessage_WhenNoDishesFound() {
//        String searchName = "UnknownDish";
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Dish> emptyPage = Page.empty();
//
//        when(dishRepository.searchByFields(searchName, pageable)).thenReturn(emptyPage);
//
//        String result = dishService.searchDishes(searchName, 1, 10);
//
//        assertEquals("❌ Блюлдо с параметром 'UnknownDish' не найден.", result);
//        verify(dishRepository, times(1)).searchByFields(searchName, pageable);
//    }
//}

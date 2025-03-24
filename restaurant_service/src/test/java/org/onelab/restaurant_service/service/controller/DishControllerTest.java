package org.onelab.restaurant_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.restaurant_service.controller.DishController;
import org.onelab.restaurant_service.service.DishService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DishControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DishService dishService;

    @InjectMocks
    private DishController dishController;

    private DishDto mockDish;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dishController).build();
        mockDish = DishDto.builder().id(1L).name("Pasta").price(1200.0).build();
    }

    @Test
    void addDish_ShouldReturnCreatedStatusAndDishId() throws Exception {
        when(dishService.save(any(DishDto.class))).thenReturn(1L);

        String requestBody = """
        {
          "name": "Pasta",
          "description": "Delicious Italian pasta",
          "price": 1200.0
        }
        """;

        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(dishService, times(1)).save(any(DishDto.class));
    }


    @Test
    void removeDish_ShouldReturnOkStatus() throws Exception {
        doNothing().when(dishService).remove(anyLong());

        mockMvc.perform(delete("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dish deleted"));

        verify(dishService, times(1)).remove(1L);
    }

    @Test
    void getDishes_ShouldReturnListOfDishes() throws Exception {
        List<DishDto> mockDishes = List.of(mockDish);
        when(dishService.getDishes(anyInt(), anyInt())).thenReturn(mockDishes);

        mockMvc.perform(get("/api/dishes?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pasta"));

        verify(dishService, times(1)).getDishes(1, 10);
    }

    @Test
    void getDishById_ShouldReturnDish() throws Exception {
        when(dishService.getDishById(anyLong())).thenReturn(mockDish);

        mockMvc.perform(get("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pasta"));

        verify(dishService, times(1)).getDishById(1L);
    }

    @Test
    void searchDishes_ShouldReturnMatchingDishes() throws Exception {
        List<DishDto> mockDishes = List.of(mockDish);
        when(dishService.searchDishes(anyString(), anyInt(), anyInt())).thenReturn(mockDishes);

        mockMvc.perform(get("/api/dishes/search?text=Pasta&page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pasta"));

        verify(dishService, times(1)).searchDishes("Pasta", 1, 10);
    }
}

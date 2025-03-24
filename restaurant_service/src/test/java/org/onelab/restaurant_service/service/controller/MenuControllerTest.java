package org.onelab.restaurant_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common_lib.dto.MenuDto;
import org.onelab.common_lib.dto.MenuRequestDto;
import org.onelab.restaurant_service.controller.MenuController;
import org.onelab.restaurant_service.service.MenuService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private MockMvc mockMvc;

    private MenuDto mockMenuDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();

        mockMenuDto = new MenuDto(1L, "Lunch Specials", List.of());
    }

    @Test
    void addMenu_ShouldReturnCreatedStatusAndMenuId() throws Exception {
        when(menuService.createMenu(any(MenuRequestDto.class))).thenReturn("1");

        String requestBody = """
        {
          "name": "Lunch Specials",
          "dishIds": [1, 2]
        }
        """;

        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(menuService, times(1)).createMenu(any(MenuRequestDto.class));
    }

    @Test
    void removeMenu_ShouldReturnNoContent() throws Exception {
        doNothing().when(menuService).removeMenu(anyLong());

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isNoContent());

        verify(menuService, times(1)).removeMenu(1L);
    }

    @Test
    void addDishesToMenu_ShouldReturnOkStatus() throws Exception {
        doNothing().when(menuService).addDishesToMenu(anyLong(), anyList());

        String requestBody = """
        [1, 2, 3]
        """;

        mockMvc.perform(post("/api/menus/1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Dishes added to menu"));

        verify(menuService, times(1)).addDishesToMenu(eq(1L), anyList());
    }

    @Test
    void removeDishesFromMenu_ShouldReturnOkStatus() throws Exception {
        doNothing().when(menuService).removeDishesFromMenu(anyLong(), anyList());

        String requestBody = """
        [1, 2]
        """;

        mockMvc.perform(delete("/api/menus/1/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Dishes removed from menu"));

        verify(menuService, times(1)).removeDishesFromMenu(eq(1L), anyList());
    }

    @Test
    void getMenu_ShouldReturnMenu() throws Exception {
        when(menuService.getMenu(anyLong())).thenReturn(mockMenuDto);

        mockMvc.perform(get("/api/menus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Lunch Specials"));

        verify(menuService, times(1)).getMenu(1L);
    }

    @Test
    void getMenus_ShouldReturnListOfMenus() throws Exception {
        when(menuService.getMenus(anyInt(), anyInt())).thenReturn(List.of(mockMenuDto));

        mockMvc.perform(get("/api/menus?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Lunch Specials"));

        verify(menuService, times(1)).getMenus(1, 10);
    }
}

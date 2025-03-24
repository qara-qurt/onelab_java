package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.MenuDto;
import org.onelab.common_lib.dto.MenuRequestDto;
import org.onelab.restaurant_service.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(MenuController.BASE_URL)
@RequiredArgsConstructor
public class MenuController {

    // Base URL
    public static final String BASE_URL = "api/menus";

    // Endpoints
    public static final String BY_ID = "/{id}";
    public static final String MENU_DISHES = "/{menuId}/dishes";


    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<String> addMenu(@Valid @RequestBody MenuRequestDto menu) {
        String menuId = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuId);
    }

    @DeleteMapping(BY_ID)
    public ResponseEntity<Void> removeMenu(@PathVariable Long id) {
        menuService.removeMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(MENU_DISHES)
    public ResponseEntity<String> addDishesToMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds) {
        menuService.addDishesToMenu(menuId, dishIds);
        return ResponseEntity.ok("Dishes added to menu");
    }

    @DeleteMapping(MENU_DISHES)
    public ResponseEntity<String> removeDishesFromMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds) {
        menuService.removeDishesFromMenu(menuId, dishIds);
        return ResponseEntity.ok("Dishes removed from menu");
    }

    @GetMapping(BY_ID)
    public ResponseEntity<MenuDto> getMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @GetMapping
    public ResponseEntity<List<MenuDto>> getMenus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(menuService.getMenus(page, size));
    }
}

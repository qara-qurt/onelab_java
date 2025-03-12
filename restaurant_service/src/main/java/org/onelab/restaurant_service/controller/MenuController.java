package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.restaurant_service.dto.MenuDto;
import org.onelab.restaurant_service.dto.MenuRequestDto;
import org.onelab.restaurant_service.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<String> addMenu(@Valid @RequestBody MenuRequestDto menu) {
        String menuId = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMenu(@PathVariable Long id) {
        menuService.removeMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{menuId}/dishes")
    public ResponseEntity<String> addDishesToMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds) {
        menuService.addDishesToMenu(menuId, dishIds);
        return ResponseEntity.ok("Dishes added to menu");
    }

    @DeleteMapping("/{menuId}/dishes")
    public ResponseEntity<String> removeDishesFromMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds) {
        menuService.removeDishesFromMenu(menuId, dishIds);
        return ResponseEntity.ok("Dishes removed from menu");
    }

    @GetMapping("/{id}")
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

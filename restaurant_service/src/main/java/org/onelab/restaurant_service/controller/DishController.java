package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.restaurant_service.dto.DishDto;
import org.onelab.restaurant_service.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @PostMapping
    public ResponseEntity<Map<String,Long>> addDish(@Valid @RequestBody DishDto dish) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id",dishService.save(dish)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeDish(@PathVariable Long id) {
        dishService.remove(id);
        return ResponseEntity.ok("Dish deleted");
    }

    @GetMapping
    public ResponseEntity<List<DishDto>> getDishes(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dishService.getDishes(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getDish(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(dishService.getDishById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DishDto>> searchDishes(@RequestParam String text,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dishService.searchDishes(text, page, size));
    }
}

package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.DishDto;
import org.onelab.restaurant_service.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(DishController.BASE_URL)
@RequiredArgsConstructor
public class DishController {

    // Base URL
    public static final String BASE_URL = "api/dishes";

    // Endpoints
    public static final String BY_ID = "/{id}";
    public static final String SEARCH = "/search";

    private final DishService dishService;

    @PostMapping
    public ResponseEntity<Map<String,Long>> addDish(@Valid @RequestBody DishDto dish) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id",dishService.save(dish)));
    }

    @DeleteMapping(BY_ID)
    public ResponseEntity<String> removeDish(@PathVariable Long id) {
        dishService.remove(id);
        return ResponseEntity.ok("Dish deleted");
    }

    @GetMapping
    public ResponseEntity<List<DishDto>> getDishes(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dishService.getDishes(page, size));
    }

    @GetMapping(BY_ID)
    public ResponseEntity<DishDto> getDish(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(dishService.getDishById(id));
    }

    @GetMapping(SEARCH)
    public ResponseEntity<List<DishDto>> searchDishes(@RequestParam String text,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dishService.searchDishes(text, page, size));
    }
}

package org.onelab.gateway_cli_service.client;

import org.onelab.gateway_cli_service.config.ClientConfig;
import org.onelab.gateway_cli_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "restaurant-service", configuration = ClientConfig.class)
public interface RestaurantClient {

    // (Dishes)
    @PostMapping("/api/dishes")
    Map<String, Long> addDish(@RequestBody DishDto dish);

    @DeleteMapping("/api/dishes/{id}")
    void removeDish(@PathVariable Long id);

    @GetMapping("/api/dishes")
    List<DishDto> getDishes(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/dishes/{id}")
    DishDto getDish(@PathVariable Long id);

    @GetMapping("/api/dishes/search")
    List<DishDto> searchDishes(@RequestParam String text,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size);


    // (Menus)
    @PostMapping("/api/menus")
    String addMenu(@RequestBody MenuRequestDto menu);

    @DeleteMapping("/api/menus/{id}")
    void removeMenu(@PathVariable Long id);

    @PostMapping("/api/menus/{menuId}/dishes")
    void addDishesToMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds);

    @DeleteMapping("/api/menus/{menuId}/dishes")
    void removeDishesFromMenu(@PathVariable Long menuId, @RequestBody List<Long> dishIds);

    @GetMapping("/api/menus/{id}")
    MenuDto getMenu(@PathVariable Long id);

    @GetMapping("/api/menus")
    List<MenuDto> getMenus(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size);


    //(Orders)
    @PostMapping("/api/orders")
    OrderDto createOrder(@RequestBody OrderRequestDto orderRequest);

    @GetMapping("/api/orders/{id}")
    OrderDto getOrder(@PathVariable Long id);

    @GetMapping("/api/orders/user/{userId}")
    List<OrderDto> getOrdersByUser(@PathVariable Long userId,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/orders")
    List<OrderDto> getOrders(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size);
}

package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.restaurant_service.dto.OrderDto;
import org.onelab.restaurant_service.dto.OrderRequestDto;
import org.onelab.restaurant_service.service.OrderService;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(OrderController.BASE_URL)
@RequiredArgsConstructor
public class OrderController {

    // Base URL
    public static final String BASE_URL = "api/orders";

    // Endpoints
    public static final String BY_ID = "/{id}";
    public static final String ORDER_BY_USER = "/user/{userId}";


    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest.getCustomerId(), orderRequest.getDishIds()));
    }

    @GetMapping(BY_ID)
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping(ORDER_BY_USER)
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId, page, size));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrders(page, size));
    }
}

package org.onelab.restaurant_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.restaurant_service.dto.OrderDto;
import org.onelab.restaurant_service.dto.OrderRequestDto;
import org.onelab.restaurant_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest.getCustomerId(), orderRequest.getDishIds()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/user/{userId}")
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

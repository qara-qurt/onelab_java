package org.onelab.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Order {
    private Long id;
    private User customer;
    private List<Dish> dishes;
    private OrderStatus status;
    private Double totalPrice;
}

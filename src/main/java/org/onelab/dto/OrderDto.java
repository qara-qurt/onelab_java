package org.onelab.dto;

import lombok.Builder;
import lombok.Data;
import org.onelab.entity.OrderStatus;

import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private UserDto customer;
    private List<DishDto> dishes;
    private OrderStatus status;
    private Double totalPrice;
}

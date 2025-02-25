package org.onelab.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private UserDto customer;
    private List<DishDto> dishes;
    private String status;
}

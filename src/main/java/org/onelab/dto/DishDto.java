package org.onelab.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DishDto {
    private Long id;
    private String name;
    private int price;
}

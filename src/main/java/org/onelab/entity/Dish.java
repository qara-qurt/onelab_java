package org.onelab.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dish {
    private Long id;
    private String name;
    private Double price;
}

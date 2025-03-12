package org.onelab.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {

    private Long id;

    @NotBlank(message = "Menu name cannot be empty")
    private String name;

    @NotEmpty(message = "Menu must contain at least one dish")
    private List<DishDto> dishes;
}

package org.onelab.camunda_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto implements Serializable {

    private Long id;

    @NotBlank(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one dish")
    private List<DishDto> dishes;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price must be greater than or equal to 0")
    private Double totalPrice;
}

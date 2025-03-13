package org.onelab.gateway_cli_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one dish ID")
    private List<Long> dishIds;
}

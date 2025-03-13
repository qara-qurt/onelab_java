package org.onelab.gateway_cli_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequestDto {

    @NotBlank(message = "Menu name cannot be empty")
    private String name;

    @NotEmpty(message = "Menu must contain at least one dish ID")
    private List<Long> dishIds;
}

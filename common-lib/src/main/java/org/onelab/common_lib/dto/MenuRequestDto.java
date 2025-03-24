package org.onelab.common_lib.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequestDto implements Serializable {

    @NotBlank(message = "Menu name cannot be empty")
    private String name;

    @NotEmpty(message = "Menu must contain at least one dish ID")
    private List<Long> dishIds;
}

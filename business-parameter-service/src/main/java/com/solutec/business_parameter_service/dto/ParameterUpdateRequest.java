package com.solutec.business_parameter_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterUpdateRequest {
    @NotBlank(message = "El valor es obligatorio")
    private String value;

    private String reason;
}
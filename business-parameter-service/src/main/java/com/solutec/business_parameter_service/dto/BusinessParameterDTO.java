package com.solutec.business_parameter_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessParameterDTO {
    private Integer parameterId;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String value;
    private String description;
    private String category;
    private String dataType;
    private LocalDate effectiveDate;
    private String changedBy;
    private Boolean isActive;
}
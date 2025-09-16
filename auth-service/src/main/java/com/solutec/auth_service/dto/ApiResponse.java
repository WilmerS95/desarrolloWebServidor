package com.solutec.auth_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}
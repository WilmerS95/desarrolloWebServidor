package com.solutec.auth_service.dto;

import lombok.*;

@Getter
@Setter
public class LoginResponse {
    private String token;
    public LoginResponse(String token) { this.token = token; }
}

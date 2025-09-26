package com.solutec.auth_service.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}

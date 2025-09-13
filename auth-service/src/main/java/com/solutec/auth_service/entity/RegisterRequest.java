package com.solutec.auth_service.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String firstName;
    private String secondOrMoreNames;
    private String firstLastName;
    private String secondLastName;
    private String marriedLastName;
    private String email;
    private String telephone;
    private String address;
}

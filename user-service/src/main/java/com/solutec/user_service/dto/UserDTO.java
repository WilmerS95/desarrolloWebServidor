package com.solutec.user_service.dto;

import lombok.*;

import java.util.Set;

@Data
public class UserDTO {
    private Long userID;
    private String username;
    private String email;
    private String firstName;
    private String secondOrMoreNames;
    private String firstLastName;
    private String secondLastName;
    private String marriedLastName;
    private String telephone;
    private String address;
    private String roleName;
    private Set<String> permissions;
    private String dpiOrPassport;
    private String photoUrl;
}

package com.solutec.auth_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prueba")
public class PruebaController {

    @GetMapping("/holamundo")
    public String getholamundo() {
        return "Prueba ";
    }


}

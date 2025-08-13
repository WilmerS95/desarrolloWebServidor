package com.solutec.desarrollo_web_server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PersonaController {

    @GetMapping("/persona")
    public Map<String, String> obtenerPersona() {
        Map<String, String> persona = new HashMap<>();
        persona.put("nombre", "Pedro");
        persona.put("apellido", "Garcia");
        return persona;
    }
}
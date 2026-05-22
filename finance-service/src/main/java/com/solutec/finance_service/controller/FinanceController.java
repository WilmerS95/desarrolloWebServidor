package com.solutec.finance_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/finances")
public class FinanceController {

    @GetMapping("/public/test")
    public ResponseEntity<?> testPublic() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Módulo de Finanzas funcional",
                "service", "finance-service",
                "port", 8086
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<?> testPrivate() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Endpoint privado - JWT validado",
                "status", "autenticado"
        ));
    }
}
package com.solutec.business_parameter_service.controller;

import com.solutec.business_parameter_service.dto.ParameterUpdateRequest;
import com.solutec.business_parameter_service.entity.BusinessParameter;
import com.solutec.business_parameter_service.entity.ParameterHistory;
import com.solutec.business_parameter_service.service.BusinessParameterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parameters")
@Slf4j
public class BusinessParameterController {

    @Autowired
    private BusinessParameterService parameterService;

    @GetMapping
    public ResponseEntity<List<BusinessParameter>> getAllParameters() {
        return ResponseEntity.ok(parameterService.getAllActive());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(parameterService.getAllCategories());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BusinessParameter>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(parameterService.getByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessParameter> getParameter(@PathVariable Integer id) {
        return parameterService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}/value")
    public ResponseEntity<Map<String, String>> getParameterValue(@PathVariable String name) {
        String value = parameterService.getParameterValue(name);
        if (value != null) {
            return ResponseEntity.ok(Map.of("name", name, "value", value));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ParameterHistory>> getParameterHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(parameterService.getParameterHistory(id));
    }

    @GetMapping("/history/recent")
    public ResponseEntity<List<ParameterHistory>> getRecentChanges() {
        return ResponseEntity.ok(parameterService.getRecentChanges());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParameter(
            @PathVariable Integer id,
            @Valid @RequestBody ParameterUpdateRequest request,
            Authentication authentication) {

        try {
            String changedBy = extractUsername(authentication);
            BusinessParameter updated = parameterService.updateParameter(
                    id,
                    request.getValue(),
                    changedBy,
                    request.getReason()
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating parameter", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createParameter(
            @Valid @RequestBody BusinessParameter parameter,
            Authentication authentication) {

        try {
            String changedBy = extractUsername(authentication);
            BusinessParameter created = parameterService.createParameter(parameter, changedBy);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error creating parameter", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParameter(
            @PathVariable Integer id,
            Authentication authentication) {

        try {
            String changedBy = extractUsername(authentication);
            parameterService.deleteParameter(id, changedBy);
            return ResponseEntity.ok(Map.of("message", "Parameter deactivated successfully"));
        } catch (Exception e) {
            log.error("Error deleting parameter", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsername(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String username = jwt.getClaimAsString("sub");
            return username != null ? username : "UNKNOWN";
        }
        return "ANONYMOUS";
    }
}
package com.solutec.business_parameter_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "BusinessParameter", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_active", columnList = "isActive")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parameterId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Size(max = 200, message = "El valor no puede exceder 200 caracteres")
    @Column(length = 200)
    private String value;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String category;

    @Column(length = 20)
    private String dataType;

    private LocalDate effectiveDate;

    @Column(length = 200)
    private String changedBy;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.effectiveDate == null) {
            this.effectiveDate = LocalDate.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
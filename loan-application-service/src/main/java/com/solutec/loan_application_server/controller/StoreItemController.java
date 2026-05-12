package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.ItemTransferDTO;
import com.solutec.loan_application_server.dto.StoreItemDTO;
import com.solutec.loan_application_server.service.StoreItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreItemController {

    private final StoreItemService storeItemService;

    @GetMapping("/items")
    public ResponseEntity<List<StoreItemDTO>> getAvailableItems() {
        try {
            List<StoreItemDTO> items = storeItemService.getAvailableStoreItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error obteniendo items de tienda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<StoreItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        try {
            List<StoreItemDTO> items = storeItemService.getItemsByCategory(categoryId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error obteniendo items por categoría", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<StoreItemDTO> getItemDetails(@PathVariable Long itemId) {
        try {
            StoreItemDTO item = storeItemService.getStoreItemDetails(itemId);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo detalles del item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/transfer")
    public ResponseEntity<?> transferItemToStore(@RequestBody ItemTransferDTO transferDTO) {
        try {
            // Validaciones básicas
            if (transferDTO.getItemId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El ID del item es requerido"));
            }

            if (transferDTO.getSalePrice() == null || transferDTO.getSalePrice().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El precio de venta debe ser mayor a cero"));
            }

            StoreItemDTO result = storeItemService.transferItemToStore(transferDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Item transferido exitosamente a la tienda",
                    "item", result
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error transfiriendo item a tienda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/transfer/overdue")
    public ResponseEntity<?> transferOverdueLoans() {
        try {
            storeItemService.transferOverdueLoansToStore();
            return ResponseEntity.ok(Map.of(
                    "message", "Proceso de transferencia de préstamos vencidos ejecutado exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error ejecutando transferencia automática", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error ejecutando el proceso"));
        }
    }

    @PostMapping("/items/{itemId}/sold")
    public ResponseEntity<?> markItemAsSold(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> saleData) {
        try {
            Long buyerUserId = Long.valueOf(saleData.get("buyerUserId").toString());
            BigDecimal saleAmount = new BigDecimal(saleData.get("saleAmount").toString());

            storeItemService.markItemAsSold(itemId, buyerUserId, saleAmount);

            return ResponseEntity.ok(Map.of(
                    "message", "Item marcado como vendido exitosamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error marcando item como vendido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
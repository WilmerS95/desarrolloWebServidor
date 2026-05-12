package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.dto.ItemTransferDTO;
import com.solutec.loan_application_server.dto.StoreItemDTO;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreItemService {

    private final ItemAvailabilityRepository itemAvailabilityRepository;
    private final ItemRepository itemRepository;
    private final LoanRepository loanRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final ItemPhotoRepository itemPhotoRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public StoreItemDTO transferItemToStore(ItemTransferDTO transferDTO) {
        Item item = itemRepository.findById(transferDTO.getItemId())
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        Loan loan = null;
        if (transferDTO.getLoanId() != null) {
            loan = loanRepository.findById(transferDTO.getLoanId())
                    .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

            if (!loan.getLoanApplication().getItem().getItemID().equals(transferDTO.getItemId())) {
                throw new RuntimeException("El item no pertenece al préstamo especificado");
            }
        }

        if (itemAvailabilityRepository.existsByItem(item)) {
            throw new RuntimeException("El item ya está disponible en la tienda");
        }

        ItemAvailability availability = new ItemAvailability();
        availability.setItem(item);
        availability.setOriginalLoan(loan);
        availability.setSalePrice(transferDTO.getSalePrice());
        availability.setStatus("AVAILABLE");
        availability.setTransferReason(transferDTO.getReason() != null ? transferDTO.getReason() : "MANUAL");
        availability.setTransferredBy(transferDTO.getTransferredBy());
        availability.setAdminComment(transferDTO.getAdminComment());
        availability.setAvailableSince(LocalDateTime.now());

        availability = itemAvailabilityRepository.save(availability);

        if (loan != null) {
            final Loan finalLoan = loan;
            loan.setStatus("DEFAULTED");
            loanRepository.save(loan);

            notifyClientItemTransferred(
                    finalLoan.getLoanApplication().getUser(),
                    item,
                    transferDTO.getReason()
            );
        }

        log.info("Item {} transferido a la tienda. Razón: {}", item.getItemID(), transferDTO.getReason());

        return convertToDTO(availability);
    }

    @Transactional
    public void transferOverdueLoansToStore() {
        log.info("Iniciando proceso de transferencia de préstamos vencidos a la tienda");

        LocalDate today = LocalDate.now();

        List<Loan> activeLoans = loanRepository.findByStatus("ACTIVE");

        int transferredCount = 0;

        for (Loan loan : activeLoans) {
            try {
                final Integer defaultDays = (loan.getDefaultDays() != null) ? loan.getDefaultDays() : 30;

                List<PaymentSchedule> overdueSchedules = paymentScheduleRepository
                        .findByLoan_LoanIdOrderByPaymentNumberAsc(loan.getLoanId())
                        .stream()
                        .filter(s -> "PENDIENTE".equals(s.getStatus()) &&
                                s.getDueDate().plusDays(defaultDays).isBefore(today))
                        .toList();

                if (!overdueSchedules.isEmpty()) {
                    Item item = loan.getLoanApplication().getItem();

                    if (!itemAvailabilityRepository.existsByItem(item)) {
                        BigDecimal salePrice = calculateSalePrice(loan);

                        ItemTransferDTO transferDTO = new ItemTransferDTO();
                        transferDTO.setItemId(item.getItemID());
                        transferDTO.setLoanId(loan.getLoanId());
                        transferDTO.setSalePrice(salePrice);
                        transferDTO.setReason("INCUMPLIMIENTO");
                        transferDTO.setAdminComment("Transferido automáticamente por vencimiento de préstamo");
                        transferDTO.setTransferredBy(null); // Sistema automático

                        transferItemToStore(transferDTO);
                        transferredCount++;

                        log.info("Préstamo {} transferido a tienda por incumplimiento. Item: {}",
                                loan.getLoanId(), item.getItemID());
                    }
                }
            } catch (Exception e) {
                log.error("Error procesando préstamo {} para transferencia a tienda",
                        loan.getLoanId(), e);
            }
        }

        log.info("Proceso de transferencia completado. Items transferidos: {}", transferredCount);
    }

    public List<StoreItemDTO> getAvailableStoreItems() {
        return itemAvailabilityRepository.findByStatusOrderByAvailableSinceDesc("AVAILABLE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StoreItemDTO> getItemsByCategory(Long categoryId) {
        return itemAvailabilityRepository.findByStatus("AVAILABLE")
                .stream()
                .filter(ia -> ia.getItem().getCategoryId().equals(categoryId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StoreItemDTO getStoreItemDetails(Long itemId) {
        ItemAvailability availability = itemAvailabilityRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item no disponible en tienda"));

        return convertToDTO(availability);
    }

    @Transactional
    public void markItemAsSold(Long itemId, Long buyerUserId, BigDecimal saleAmount) {
        ItemAvailability availability = itemAvailabilityRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en tienda"));

        if (!"AVAILABLE".equals(availability.getStatus())) {
            throw new RuntimeException("El item no está disponible para venta");
        }

        availability.setStatus("SOLD");
        availability.setSoldDate(LocalDateTime.now());
        availability.setSoldTo(buyerUserId);
        availability.setSaleAmount(saleAmount);

        itemAvailabilityRepository.save(availability);

        log.info("Item {} marcado como vendido a usuario {}", itemId, buyerUserId);
    }

    private BigDecimal calculateSalePrice(Loan loan) {
        BigDecimal balance = loan.getBalance();
        BigDecimal markup = new BigDecimal("1.10"); // 10% sobre el saldo

        return balance.multiply(markup).setScale(2, RoundingMode.HALF_UP);
    }

    private void notifyClientItemTransferred(User user, Item item, String reason) {
        try {
            String reasonText = switch (reason) {
                case "INCUMPLIMIENTO" -> "por incumplimiento en los pagos del préstamo";
                case "VENCIMIENTO" -> "por vencimiento del plazo del préstamo";
                case "MANUAL" -> "por decisión administrativa";
                default -> "";
            };

            String subject = "Notificación: tu artículo ha sido transferido a la tienda";
            String message = String.format("""
                <html>
                  <body>
                    <p>Estimado/a <strong>%s</strong>,</p>
                    <p>Queremos informarte que tu artículo <strong>%s</strong> ha sido transferido a nuestra tienda %s.</p>
                    <p>Motivo: <em>%s</em></p>
                    <p>Puedes comunicarte con nosotros para más información o realizar cualquier consulta adicional.</p>
                    <br>
                    <p>Atentamente,</p>
                    <p><strong>Equipo Solutec</strong></p>
                  </body>
                </html>
                """,
                    user.getUsername(),
                    item.getNameItem(),
                    reasonText,
                    reasonText
            );

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendEmail(user.getEmail(), subject, message);
                log.info("Correo enviado a {} sobre transferencia de item {}", user.getEmail(), item.getItemID());
            } else {
                log.warn("El usuario {} no tiene correo registrado, no se envió notificación por email", user.getUserID());
            }

        } catch (Exception e) {
            log.error("Error al notificar cliente sobre transferencia de item", e);
        }
    }


    private StoreItemDTO convertToDTO(ItemAvailability availability) {
        Item item = availability.getItem();
        StoreItemDTO dto = new StoreItemDTO();

        dto.setItemId(item.getItemID());
        dto.setNameItem(item.getNameItem());
        dto.setBrand(item.getBrand());
        dto.setDescription(item.getDescription());
        dto.setSpecification(item.getSpecification());
        dto.setCategoryId(item.getCategoryId());
        dto.setSalePrice(availability.getSalePrice());
        dto.setTransferReason(availability.getTransferReason());
        dto.setAvailableSince(availability.getAvailableSince());
        dto.setItemStatus(availability.getStatus());
        dto.setIsSold("SOLD".equals(availability.getStatus()));

        List<ItemPhoto> photos = itemPhotoRepository.findByItem(item);
        if (!photos.isEmpty()) {
            dto.setPhotos(photos.get(0).getPhotoPath());
        }

        if (availability.getOriginalLoan() != null) {
            dto.setOriginalLoanAmount(availability.getOriginalLoan().getLoanAmount());
        }

        return dto;
    }
}
package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.dto.ItemForTransferDTO;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceExtension {

    private final LoanRepository loanRepository;
    private final ItemAvailabilityRepository itemAvailabilityRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final CategoryRepository categoryRepository; // Si existe

    public List<ItemForTransferDTO> getItemsAvailableForTransfer() {
        List<ItemForTransferDTO> result = new ArrayList<>();

        List<Loan> activeLoans = loanRepository.findByStatusIn(List.of("ACTIVE", "DEFAULTED"));

        for (Loan loan : activeLoans) {
            try {
                Item item = loan.getLoanApplication().getItem();

                boolean alreadyInStore = itemAvailabilityRepository.existsByItem(item);

                if (!alreadyInStore) {
                    PaymentInfo paymentInfo = getPaymentInfo(loan);

                    ItemForTransferDTO dto = new ItemForTransferDTO();
                    dto.setItemId(item.getItemID());
                    dto.setLoanId(loan.getLoanId());
                    dto.setItemName(item.getNameItem());
                    dto.setBrand(item.getBrand());

                    String categoryName = getCategoryName(item.getCategoryId());
                    dto.setCategoryName(categoryName);

                    dto.setLoanAmount(loan.getLoanAmount());
                    dto.setBalance(loan.getBalance());

                    User client = loan.getLoanApplication().getUser();
                    String clientName = client.getFirstName() + " " + client.getFirstLastName();
                    dto.setClientName(clientName);

                    dto.setLoanStatus(loan.getStatus());
                    dto.setHasOverduePayments(paymentInfo.hasOverduePayments);
                    dto.setDaysOverdue(paymentInfo.daysOverdue);

                    result.add(dto);
                }
            } catch (Exception e) {
                log.error("Error procesando préstamo {} para transferencia", loan.getLoanId(), e);
            }
        }

        result.sort((a, b) -> {
            if (a.getHasOverduePayments() && !b.getHasOverduePayments()) return -1;
            if (!a.getHasOverduePayments() && b.getHasOverduePayments()) return 1;
            return b.getDaysOverdue().compareTo(a.getDaysOverdue());
        });

        log.info("Items disponibles para transferir: {}", result.size());
        return result;
    }

    private PaymentInfo getPaymentInfo(Loan loan) {
        PaymentInfo info = new PaymentInfo();
        info.hasOverduePayments = false;
        info.daysOverdue = 0;

        try {
            LocalDate today = LocalDate.now();

            List<PaymentSchedule> pendingSchedules = paymentScheduleRepository
                    .findByLoan_LoanIdOrderByPaymentNumberAsc(loan.getLoanId())
                    .stream()
                    .filter(s -> "PENDIENTE".equals(s.getStatus()))
                    .toList();

            for (PaymentSchedule schedule : pendingSchedules) {
                if (schedule.getDueDate().isBefore(today)) {
                    info.hasOverduePayments = true;
                    long days = ChronoUnit.DAYS.between(schedule.getDueDate(), today);
                    if (days > info.daysOverdue) {
                        info.daysOverdue = (int) days;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obteniendo info de pagos", e);
        }

        return info;
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "Sin categoría";

        return switch (categoryId.intValue()) {
            case 1 -> "Electrónica";
            case 2 -> "Joyería";
            case 3 -> "Vehículos";
            case 4 -> "Otros";
            default -> "Sin categoría";
        };
    }

    private static class PaymentInfo {
        boolean hasOverduePayments;
        int daysOverdue;
    }
}
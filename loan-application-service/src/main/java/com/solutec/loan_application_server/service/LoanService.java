package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.dto.LoanDTO;
import com.solutec.loan_application_server.entity.Loan;
import com.solutec.loan_application_server.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    public List<LoanDTO> getUserActiveLoans(Long userId) {
        return loanRepository.findByLoanApplication_User_UserIDAndStatusIn(
                        userId,
                        List.of("ACTIVO", "OVERDUE")
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDTO> getUserLoanHistory(Long userId) {
        return loanRepository.findByLoanApplication_User_UserID(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoanDTO getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        return convertToDTO(loan);
    }

    public List<LoanDTO> getAllActiveLoans() {
        return loanRepository.findByStatusIn(List.of("ACTIVO", "OVERDUE"))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setLoanApplicationId(loan.getLoanApplication().getLoanApplicationID());
        dto.setItemName(loan.getLoanApplication().getItem().getNameItem());
        dto.setItemBrand(loan.getLoanApplication().getItem().getBrand());
        dto.setApprovalDate(loan.getApprovalDate());
        dto.setContractGeneratedDate(loan.getContractGeneratedDate()); // Cambio importante
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTerm(loan.getTerm());
        dto.setDueDate(loan.getDueDate());
        dto.setStatus(loan.getStatus());
        dto.setBalance(loan.getBalance());
        dto.setContractNumber(loan.getContractNumber());
        dto.setLatePaymentFee(loan.getLatePaymentFee());
        dto.setTotalInterest(loan.getTotalInterest());
        dto.setTotalAmount(loan.getTotalAmount());
        dto.setGracePeriodDays(loan.getGracePeriodDays());
        dto.setDefaultDays(loan.getDefaultDays());
        return dto;
    }
}
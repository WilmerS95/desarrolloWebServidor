package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.LoanApplication;
import com.solutec.loan_application_server.entity.ProposedInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposedInstallmentRepository extends JpaRepository<ProposedInstallment, Long> {
    List<ProposedInstallment> findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(Long loanApplicationId);
    List<ProposedInstallment> findByLoanApplication_LoanApplicationID(Long loanApplicationID);
    List<ProposedInstallment> findByLoanApplication(LoanApplication loanApplication);
    List<ProposedInstallment> findByInstallmentNumber(Integer installmentNumber);
    List<ProposedInstallment> findByStatus(String status);
    List<ProposedInstallment> findByLoanApplicationAndStatus(LoanApplication loanApplication, String status);
}

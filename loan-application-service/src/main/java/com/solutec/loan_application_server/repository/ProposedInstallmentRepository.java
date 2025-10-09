package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.ProposedInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposedInstallmentRepository extends JpaRepository<ProposedInstallment, Long> {
    List<ProposedInstallment> findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(Long loanApplicationId);
}

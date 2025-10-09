package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByLoanApplicationLoanApplicationID(Long loanApplicationId);
}
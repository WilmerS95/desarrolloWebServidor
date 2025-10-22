package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoan_LoanIdOrderByPaymentDateDesc(Long loanId);
    List<Payment> findByStatusOrderByPaymentDateDesc(String status);
    List<Payment> findByStatus(String status);
}
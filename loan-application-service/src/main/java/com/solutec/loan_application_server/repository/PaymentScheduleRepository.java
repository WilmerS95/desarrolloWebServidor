package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    List<PaymentSchedule> findByLoan_LoanIdOrderByPaymentNumberAsc(Long loanId);

    @Query("SELECT ps FROM PaymentSchedule ps WHERE ps.dueDate = :date AND ps.status = 'PENDIENTE' AND ps.notificationSent = false")
    List<PaymentSchedule> findPaymentsDueTomorrow(@Param("date") LocalDate date);

    @Query("SELECT ps FROM PaymentSchedule ps WHERE ps.dueDate < :date AND ps.status = 'PENDIENTE' AND ps.collectorNotified = false")
    List<PaymentSchedule> findOverduePayments(@Param("date") LocalDate date);

    List<PaymentSchedule> findByLoan_LoanIdAndPaymentNumber(Long loanId, Integer paymentNumber);

    List<PaymentSchedule> findByLoan_LoanIdAndStatus(Long loanId, String status);

    List<PaymentSchedule> findByStatusAndDueDateBefore(String status, LocalDate date);
}
package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {}
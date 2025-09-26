package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }

package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {}
package com.solutec.business_parameter_service.repository;

import com.solutec.business_parameter_service.entity.ParameterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParameterHistoryRepository extends JpaRepository<ParameterHistory, Long> {

    List<ParameterHistory> findByParameterIdOrderByChangedAtDesc(Integer parameterId);

    List<ParameterHistory> findTop10ByOrderByChangedAtDesc();
}
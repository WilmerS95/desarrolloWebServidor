package com.solutec.business_parameter_service.repository;

import com.solutec.business_parameter_service.entity.BusinessParameter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BusinessParameterRepository extends JpaRepository<BusinessParameter, Integer> {

    Optional<BusinessParameter> findByNameAndIsActiveTrue(String name);

    List<BusinessParameter> findByCategoryAndIsActiveTrue(String category);

    List<BusinessParameter> findByIsActiveTrue();

    boolean existsByName(String name);

    @Query("SELECT bp FROM BusinessParameter bp WHERE bp.name = ?1 AND bp.isActive = true ORDER BY bp.effectiveDate DESC")
    Optional<BusinessParameter> findLatestByName(String name);

    @Query("SELECT DISTINCT bp.category FROM BusinessParameter bp WHERE bp.isActive = true")
    List<String> findAllActiveCategories();
}
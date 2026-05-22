package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Standing;
import com.solutec.competition_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandingRepository extends JpaRepository<Standing, Long> {
    List<Standing> findByGroup(Group group);
}
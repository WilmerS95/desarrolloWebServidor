package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Goal;
import com.solutec.competition_service.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMatch(Match match);
}
package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Tournament;
import com.solutec.competition_service.entity.enums.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStatus(TournamentStatus status);
    List<Tournament> findByNameContainingIgnoreCase(String name);
}
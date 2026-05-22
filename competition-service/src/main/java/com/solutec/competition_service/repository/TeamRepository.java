package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Team;
import com.solutec.competition_service.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTournament(Tournament tournament);
    List<Team> findByNameContainingIgnoreCase(String name); // corregido
}
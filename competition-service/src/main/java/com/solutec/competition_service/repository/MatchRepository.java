package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Match;
import com.solutec.competition_service.entity.Tournament;
import com.solutec.competition_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournament(Tournament tournament);
    List<Match> findByGroup(Group group);
}
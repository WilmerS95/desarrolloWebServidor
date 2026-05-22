package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Player;
import com.solutec.competition_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeam(Team team);
    List<Player> findByDpi(String dpi);
}
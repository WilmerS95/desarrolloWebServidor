package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Group;
import com.solutec.competition_service.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByTournament(Tournament tournament);
}
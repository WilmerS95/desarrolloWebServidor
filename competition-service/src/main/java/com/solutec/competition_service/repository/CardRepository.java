package com.solutec.competition_service.repository;

import com.solutec.competition_service.entity.Card;
import com.solutec.competition_service.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByMatch(Match match);
}
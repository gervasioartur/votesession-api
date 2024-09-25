package com.votesession.repository;

import com.votesession.domain.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("SELECT a FROM Agenda a " +
            " INNER JOIN VotingSession v on v.agenda.id = a.id " +
            "WHERE v.endDate >= :givenDate ")
    List<Agenda> findAllActiveSession(@Param("givenDate") LocalDateTime givenDate);
}

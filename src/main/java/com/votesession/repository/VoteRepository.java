package com.votesession.repository;

import com.votesession.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Finds the vote by the user id (cpf) and agenda id
    Optional<Vote> findByUserIdAndAgenda_Id(String userId, Long agendaId);
}

package com.votesession.repository;

import com.votesession.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndAgenda_Id(String userId, Long agendaId);
}

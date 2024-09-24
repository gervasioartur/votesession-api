package com.votesession.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votesession.domain.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

}

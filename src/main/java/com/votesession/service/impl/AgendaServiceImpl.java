package com.votesession.service.impl;

import org.springframework.stereotype.Service;

import com.votesession.domain.Agenda;
import com.votesession.repository.AgendaRepository;
import com.votesession.service.contracts.AgendaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository repository;

    @Override
    public Agenda create(Agenda agenda) {
        return this.repository.save(agenda);
    }
}

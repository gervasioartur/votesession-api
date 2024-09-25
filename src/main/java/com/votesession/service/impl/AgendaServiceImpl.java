package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.repository.AgendaRepository;
import com.votesession.service.contracts.AgendaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    private final AgendaRepository repository;

    @Override
    public Agenda create(Agenda agenda) {
        agenda.setActive(true);
        return this.repository.save(agenda);
    }

    @Override
    public List<Agenda> readAll() {
        return this.repository.findAll();
    }
}

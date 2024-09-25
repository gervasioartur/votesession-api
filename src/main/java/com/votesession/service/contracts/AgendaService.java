package com.votesession.service.contracts;

import com.votesession.domain.entity.Agenda;

import java.util.List;

public interface AgendaService {
    Agenda create(Agenda agenda);

    List<Agenda> readAll();
}
package com.votesession.api.controller;

import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.api.dto.Response;
import com.votesession.domain.Agenda;
import com.votesession.service.contracts.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agendas")
@RequiredArgsConstructor
public class AgendaController {
    private final AgendaService service;
    private final ModelMapper mapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> create(@Valid @RequestBody CreateAgendaRequest request) {
        Agenda agenda = this.mapper.map(request, Agenda.class);
        this.service.create(agenda);
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Created.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

package com.votesession.api.agneda;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.votesession.api.dto.Response;
import com.votesession.domain.Agenda;
import com.votesession.service.contracts.AgendaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agendas")
@RequiredArgsConstructor
public class AgendaController {
    private final AgendaService service;
    private final ModelMapper mapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> create(@RequestBody CreateAgendaRequest request) {
        try {
            Agenda agenda = this.mapper.map(request, Agenda.class);
            this.service.create(agenda);

            Response response = new  Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "Created.");

            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (Exception e) {
            Response response = new  Response(HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ,HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "An unexpected error occurred. Please try again later.");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

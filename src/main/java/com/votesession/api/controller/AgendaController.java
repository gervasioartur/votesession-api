package com.votesession.api.controller;

import com.votesession.api.dto.AgendaResponse;
import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.api.dto.OpenVotingSessionRequest;
import com.votesession.api.dto.Response;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;
import com.votesession.service.contracts.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Agendas")
@RequiredArgsConstructor
@RequestMapping("/agendas")
public class AgendaController {
    private final AgendaService service;
    private final ModelMapper mapper;

    @Operation(summary = "Create new Agenda")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns successful message"),
            @ApiResponse(responseCode = "400", description = "Bad request happened"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> create(@Valid @RequestBody CreateAgendaRequest request) {
        Agenda agenda = this.mapper.map(request, Agenda.class);
        this.service.create(agenda);
        Response response = new Response(HttpStatus.CREATED.value(),
                HttpStatus.CREATED.name(),
                "Created.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Read all Agendas")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a all Agendas"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> readAll() {
        List<Agenda> agendas = this.service.readAll();
        List<AgendaResponse> agendasResponse = agendas
                .stream()
                .map(element -> mapper.map(element, AgendaResponse.class))
                .toList();

        Response response = new Response(HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                agendasResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Open voting session")
    @PostMapping(value = "/{agendaId}/session", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> openSession(@PathVariable Long agendaId,
                                                @RequestBody OpenVotingSessionRequest request) {
        VotingSession votingSession = VotingSession
                .builder()
                .agenda(Agenda.builder().id(agendaId).build())
                .build();

        this.service.openSession(votingSession, request.getDuration());
        return null;
    }
}

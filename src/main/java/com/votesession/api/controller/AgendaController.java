package com.votesession.api.controller;

import com.votesession.api.dto.*;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
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

import java.time.format.DateTimeFormatter;
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
                .map(agenda -> {
                    List<VotingSessionResponse> votingSessionsResponse = agenda.getVotingSessions()
                            .stream()
                            .map(votingSession -> mapper.map(votingSession, VotingSessionResponse.class))
                            .toList();
                    AgendaResponse agendaResponse = mapper.map(agenda, AgendaResponse.class);
                    agendaResponse.setOpenedVotingSessions(votingSessionsResponse);

                    return agendaResponse;
                })
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
                                                @Valid @RequestBody OpenVotingSessionRequest request) {
        VotingSession votingSession = VotingSession
                .builder()
                .agenda(Agenda.builder().id(agendaId).build())
                .build();

        votingSession = this.service.openSession(votingSession, request.getDuration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at time' HH:mm");
        String formattedStartDate = votingSession.getStartDate().format(formatter);
        String formattedEndDate = votingSession.getEndDate().format(formatter);

        Response response = new Response(HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "Voting opened successfully, it starts on  "
                        + formattedStartDate + " and ends on  " + formattedEndDate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create new Agenda")
    @PostMapping(value = "/{userIdentity}/vote", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message"),
            @ApiResponse(responseCode = "400", description = "Bad request happened"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "409", description = "Conflict"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> vote(@PathVariable(name = "userIdentity") String userIdentity,
                                         @Valid @RequestBody VoteRequest request) {
        Vote vote =  Vote
                .builder()
                .userId(userIdentity)
                .agenda(Agenda.builder().id(request.getAgendaId()).build())
                .vote(request.getVote())
                .build();

        this.service.vote(vote);
        Response response = new Response(HttpStatus.OK.value(),
                HttpStatus.OK.name(), "User vote saved Successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

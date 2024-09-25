package com.votesession.mocks;

import com.github.javafaker.Faker;
import com.votesession.api.dto.AgendaResponse;
import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.VotingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MocksFactory {
    public static final Faker faker = new Faker();

    public static Agenda agendaWithNoIdFactory() {
        return Agenda
                .builder()
                .title(faker.lorem().word())
                .description(faker.lorem().paragraph())
                .active(true)
                .build();
    }

    public static Agenda agendaWithNoIdFactory(CreateAgendaRequest request) {
        return Agenda
                .builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .active(true)
                .build();
    }

    public static Agenda agendaWithIdFactory(Agenda agenda) {
        return Agenda
                .builder()
                .id(faker.random().nextLong())
                .title(agenda.getTitle())
                .description(agenda.getDescription())
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }

    public static Agenda agendaWithIdFactory() {
        return Agenda
                .builder()
                .id(faker.random().nextLong())
                .title(faker.lorem().word())
                .description(faker.lorem().paragraph())
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }

    public static List<AgendaResponse> agendasResponseFactory(List<Agenda> agendas) {
        return agendas.stream()
                .map(MocksFactory::agendaResponseFactory)
                .toList();
    }

    public static AgendaResponse agendaResponseFactory(Agenda agenda) {
        return AgendaResponse
                .builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .description(agenda.getDescription())
                .build();
    }

    public static CreateAgendaRequest createAgendaRequestFactory() {
        return new CreateAgendaRequest(faker.lorem().word(), faker.lorem().paragraph());
    }

    public static VotingSession votingSessionWithNoIdFactory() {
        return VotingSession
                .builder()
                .agenda(agendaWithIdFactory())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMinutes(1))
                .active(true)
                .build();
    }

}

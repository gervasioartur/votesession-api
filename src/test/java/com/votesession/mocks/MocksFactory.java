package com.votesession.mocks;

import com.github.javafaker.Faker;
import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.domain.Agenda;

import java.time.LocalDate;

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

    public static CreateAgendaRequest createAgendaRequestFactory() {
        return new CreateAgendaRequest(faker.lorem().word(), faker.lorem().paragraph());
    }

}

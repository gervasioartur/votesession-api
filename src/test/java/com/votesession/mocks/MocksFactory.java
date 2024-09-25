package com.votesession.mocks;

import com.github.javafaker.Faker;
import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.domain.Agenda;

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
                .title(agenda.getTitle())
                .description(agenda.getDescription())
                .active(true)
                .createdAt(agenda.getCreatedAt())
                .updatedAt(agenda.getUpdatedAt())
                .build();
    }

    public static CreateAgendaRequest createAgendaRequestFactory() {
        return new CreateAgendaRequest(faker.lorem().word(), faker.lorem().paragraph());
    }

}

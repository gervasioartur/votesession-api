package com.votesession.mocks;

import com.votesession.domain.Agenda;

public class MocksFactory {
    public static Agenda agendaWithNoIdFactory() {
        return Agenda
                .builder()
                .title("Any tittle")
                .description("Any_Description")
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
}

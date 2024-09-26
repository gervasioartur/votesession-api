package com.votesession.mocks;

import com.github.javafaker.Faker;
import com.votesession.api.dto.CreateAgendaRequest;
import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Agenda agenda = Agenda
                .builder()
                .id(faker.random().nextLong())
                .title(faker.lorem().word())
                .description(faker.lorem().paragraph())
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();

        Set<VotingSession> votingSessions = new HashSet<>(List.of(MocksFactory.votingSessionWithIdFactory(agenda)
                , MocksFactory.votingSessionWithIdFactory(agenda)));

        Vote inFavor = MocksFactory.voteWithIdFactory(agenda);
        inFavor.setVote("Sim");
        Vote against = MocksFactory.voteWithIdFactory(agenda);
        against.setVote("Não");
        Set<Vote> votes = new HashSet<>(List.of(inFavor, against));

        agenda.setVotingSessions(votingSessions);
        agenda.setVotes(votes);

        return agenda;
    }

    public static CreateAgendaRequest createAgendaRequestFactory() {
        return new CreateAgendaRequest(faker.lorem().word(), faker.lorem().paragraph());
    }

    public static VotingSession votingSessionWithNoIdFactory(int duration) {
        duration = duration == 0 ? GeneralIntEnum.DEFAULT_DURATION_MIN.getValue() : duration;
        return VotingSession
                .builder()
                .agenda(MocksFactory.agendaWithIdFactory())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMinutes(duration))
                .active(true)
                .build();
    }

    public static VotingSession votingSessionWithIdFactory(VotingSession votingSession) {
        return VotingSession
                .builder()
                .id(faker.random().nextLong())
                .agenda(votingSession.getAgenda())
                .startDate(votingSession.getStartDate())
                .endDate(votingSession.getEndDate())
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }

    public static VotingSession votingSessionWithIdFactory(Agenda agenda) {
        return VotingSession
                .builder()
                .id(faker.random().nextLong())
                .agenda(agenda)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(faker.random().nextInt(10)))
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }


    public static Vote voteWithNoIdFactory(String document) {
        return Vote
                .builder()
                .userId(document)
                .agenda(MocksFactory.agendaWithIdFactory())
                .vote(faker.lorem().word())
                .build();
    }

    public static Vote voteWithIdFactory(Vote vote) {
        return Vote
                .builder()
                .id(faker.random().nextLong())
                .userId(vote.getUserId())
                .agenda(vote.getAgenda())
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }

    public static Vote voteWithIdFactory(Agenda agenda) {
        return Vote
                .builder()
                .id(faker.random().nextLong())
                .userId(faker.lorem().word())
                .agenda(agenda)
                .active(true)
                .createdAt(LocalDate.now().atStartOfDay())
                .createdAt(LocalDate.now().atStartOfDay())
                .build();
    }
}

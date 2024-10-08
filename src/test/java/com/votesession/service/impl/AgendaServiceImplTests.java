package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.domain.entity.Vote;
import com.votesession.domain.entity.VotingSession;
import com.votesession.domain.enums.GeneralIntEnum;
import com.votesession.domain.exception.BusinessException;
import com.votesession.domain.exception.ConflictException;
import com.votesession.domain.exception.NotFoundException;
import com.votesession.domain.model.VotingResults;
import com.votesession.mocks.MocksFactory;
import com.votesession.repository.AgendaRepository;
import com.votesession.repository.VoteRepository;
import com.votesession.repository.VotingSessionRepository;
import com.votesession.service.contracts.AgendaService;
import com.votesession.service.contracts.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class AgendaServiceImplTests {
    @Autowired
    AgendaService service;

    @MockBean
    UserService userService;

    @MockBean
    AgendaRepository repository;

    @MockBean
    VoteRepository voteRepository;

    @MockBean
    VotingSessionRepository votingSessionRepository;

    @Test
    @DisplayName("Should create new agenda")
    void shouldCreateNewAgenda() {
        Agenda agenda = MocksFactory.agendaWithNoIdFactory();
        Agenda savedAgenda = MocksFactory.agendaWithIdFactory(agenda);

        Mockito.when(this.repository.save(agenda)).thenReturn(savedAgenda);

        Agenda result = this.service.create(agenda);

        Mockito.verify(this.repository, Mockito.times(1)).save(agenda);
        Assertions.assertThat(result.getId()).isEqualTo(savedAgenda.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(savedAgenda.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(savedAgenda.getDescription());
        Assertions.assertThat(result.isActive()).isTrue();
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(savedAgenda.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(savedAgenda.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return  list of agendas")
    void shouldReturnListOfAgendas() {
        List<Agenda> agendas = List.of(MocksFactory.agendaWithIdFactory(), MocksFactory.agendaWithIdFactory());

        Mockito.when(this.repository.findAll()).thenReturn(agendas);
        List<Agenda> result = this.service.readAll();

        Mockito.verify(this.repository, Mockito.times(1)).findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(agendas.size());
        Assertions.assertThat(result.getFirst().getId()).isEqualTo(agendas.getFirst().getId());
        Assertions.assertThat(result.getLast().getId()).isEqualTo(agendas.getLast().getId());
    }

    @Test
    @DisplayName("Should throw NotFound if agenda does not exist in the system on open voting session")
    void shouldThrowNotFoundIfAgendaDoesNotExistInTheSystemOnOpenVotingSession() {
        int duration = (int) MocksFactory.faker.number().randomNumber();
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(duration);

        Mockito.when(repository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() -> this.service.openSession(votingSession, duration));

        Assertions.assertThat(exception).isInstanceOf(NotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo(
                "Could not find agenda with id " + votingSession.getAgenda().getId());
        Mockito.verify(repository, Mockito.times(1))
                .findById(votingSession.getAgenda().getId());
    }

    @Test
    @DisplayName("Should open session with default duration time if duration is not set")
    void ShouldOpenSessionWithDefaultDurationTimeIfDurationIsNotSet() {
        int duration = 0;
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(duration);
        Agenda agenda = votingSession.getAgenda();

        VotingSession savedVotingSession = MocksFactory.votingSessionWithIdFactory(votingSession);

        Mockito.when(repository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.of(agenda));
        Mockito.when(votingSessionRepository.save(Mockito.any(VotingSession.class))).thenReturn(savedVotingSession);

        VotingSession result = this.service.openSession(votingSession, duration);

        int difference = result.getEndDate().compareTo(result.getStartDate());
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(duration).isEqualTo(0);
        Assertions.assertThat(difference).isEqualTo(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue());
        Mockito.verify(this.repository, Mockito.times(1)).findById(agenda.getId());
        Mockito.verify(this.votingSessionRepository, Mockito.times(1))
                .save(Mockito.any(VotingSession.class));
    }

    @Test
    @DisplayName("Should open session with informed duration time if duration is not set")
    void ShouldOpenSessionInformedDurationTimeIfDurationIsNotSet() {
        int duration = MocksFactory.faker.number().numberBetween(1, Integer.MAX_VALUE);
        VotingSession votingSession = MocksFactory.votingSessionWithNoIdFactory(duration);
        Agenda agenda = votingSession.getAgenda();

        VotingSession savedVotingSession = MocksFactory.votingSessionWithIdFactory(votingSession);

        Mockito.when(repository.findById(votingSession.getAgenda().getId())).thenReturn(Optional.of(agenda));
        Mockito.when(votingSessionRepository.save(Mockito.any(VotingSession.class))).thenReturn(savedVotingSession);

        VotingSession result = this.service.openSession(votingSession, duration);

        int difference = result.getEndDate().compareTo(result.getStartDate());
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(duration).isGreaterThan(0);
        Assertions.assertThat(difference).isGreaterThan(GeneralIntEnum.DEFAULT_DURATION_MIN.getValue());
        Mockito.verify(this.repository, Mockito.times(1)).findById(agenda.getId());
        Mockito.verify(this.votingSessionRepository, Mockito.times(1))
                .save(Mockito.any(VotingSession.class));
    }

    @Test
    @DisplayName("Should throw business exception if the user is unable to vote")
    void shouldThrowBusinessExceptionIfIsUnableToVote() {
        String document = MocksFactory.faker.lorem().word();
        Vote vote = MocksFactory.voteWithNoIdFactory(document);

        Mockito.when(this.userService.isAbleToVote(document)).thenReturn(false);

        Throwable exception = Assertions.catchThrowable(() -> this.service.vote(vote));

        Assertions.assertThat(exception).isInstanceOf(BusinessException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("User unable to vote.");
        Mockito.verify(this.userService, Mockito.times(1)).isAbleToVote(document);
    }

    @Test
    @DisplayName("Should throw NotFound Exception if agenda does not exist")
    void shouldThrowNotFoundExceptionIfAgendaDoesNotExist() {
        String document = MocksFactory.faker.lorem().word();
        Vote vote = MocksFactory.voteWithNoIdFactory(document);

        Mockito.when(this.userService.isAbleToVote(document)).thenReturn(true);
        Mockito.when(this.repository.findById(vote.getAgenda().getId())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() -> this.service.vote(vote));

        Assertions.assertThat(exception).isInstanceOf(NotFoundException.class);
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Unable to find agenda with id : " + vote.getAgenda().getId());
        Mockito.verify(this.userService, Mockito.times(1)).isAbleToVote(document);
        Mockito.verify(this.repository, Mockito.times(1)).findById(vote.getAgenda().getId());

    }

    @Test
    @DisplayName("Should throw Conflict exception if user as already voted")
    void shouldThrowConflictExceptionIfUserAsAlreadyVoted() {
        String document = MocksFactory.faker.lorem().word();
        Vote vote = MocksFactory.voteWithNoIdFactory(document);

        Vote savedVote = MocksFactory.voteWithIdFactory(vote);

        Mockito.when(this.userService.isAbleToVote(document)).thenReturn(true);
        Mockito.when(this.repository.findById(vote.getAgenda().getId())).thenReturn(Optional.of(vote.getAgenda()));

        Mockito.when(this.voteRepository
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId())).thenReturn(Optional.of(savedVote));

        Throwable exception = Assertions.catchThrowable(() -> this.service.vote(vote));

        Assertions.assertThat(exception).isInstanceOf(ConflictException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("User already voted.");
        Mockito.verify(this.userService, Mockito.times(1)).isAbleToVote(document);
        Mockito.verify(this.voteRepository, Mockito.times(1))
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId());
        Mockito.verify(this.repository, Mockito.times(1)).findById(vote.getAgenda().getId());
    }

    @Test
    @DisplayName("Should throw Business exception if there is no opened voting session")
    void shouldThrowBusinessExceptionIfThereIsOpenedVotingException() {
        String document = MocksFactory.faker.lorem().word();
        Vote vote = MocksFactory.voteWithNoIdFactory(document);

        LocalDateTime now = LocalDateTime.now();
        vote.getAgenda().getVotingSessions().forEach(session -> session.setEndDate(now.minusMinutes(2)));

        Mockito.when(this.userService.isAbleToVote(document)).thenReturn(true);
        Mockito.when(this.repository.findById(vote.getAgenda().getId())).thenReturn(Optional.of(vote.getAgenda()));
        Mockito.when(this.voteRepository
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() -> this.service.vote(vote));

        Assertions.assertThat(exception).isInstanceOf(BusinessException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("Could not find active voting session.");
        Mockito.verify(this.userService, Mockito.times(1)).isAbleToVote(document);
        Mockito.verify(this.voteRepository, Mockito.times(1))
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId());
        Mockito.verify(this.repository, Mockito.times(1)).findById(vote.getAgenda().getId());
    }

    @Test
    @DisplayName("Should save user vote")
    void shouldSaveUserVote() {
        String document = MocksFactory.faker.lorem().word();
        Vote vote = MocksFactory.voteWithNoIdFactory(document);

        Mockito.when(this.userService.isAbleToVote(document)).thenReturn(true);
        Mockito.when(this.repository.findById(vote.getAgenda().getId())).thenReturn(Optional.of(vote.getAgenda()));

        Mockito.when(this.voteRepository
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId())).thenReturn(Optional.empty());

        this.service.vote(vote);


        Mockito.verify(this.userService, Mockito.times(1)).isAbleToVote(document);
        Mockito.verify(this.voteRepository, Mockito.times(1))
                .findByUserIdAndAgenda_Id(document, vote.getAgenda().getId());
        Mockito.verify(this.repository, Mockito.times(1)).findById(vote.getAgenda().getId());
        Mockito.verify(this.voteRepository, Mockito.times(1)).save(Mockito.any(Vote.class));
    }

    @Test
    @DisplayName("Should return voting  results")
    void shouldReturnVotingResults() {
        List<Agenda> agendas = List.of(MocksFactory.agendaWithIdFactory(), MocksFactory.agendaWithIdFactory());

        Mockito.when(this.repository.findAll()).thenReturn(agendas);

        List<VotingResults> results = this.service.readResults();

        Assertions.assertThat(results).isNotNull();
        Assertions.assertThat(results.size()).isEqualTo(agendas.size());
        Mockito.verify(this.repository, Mockito.times(1)).findAll();
    }
}

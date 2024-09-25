package com.votesession.service.impl;

import com.votesession.domain.entity.Agenda;
import com.votesession.mocks.MocksFactory;
import com.votesession.repository.AgendaRepository;
import com.votesession.service.contracts.AgendaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
public class AgendaServiceTests {
    @Autowired
    AgendaService service;

    @MockBean
    AgendaRepository repository;

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
        List<Agenda> agendas =  List.of(MocksFactory.agendaWithIdFactory(),MocksFactory.agendaWithIdFactory());

        Mockito.when(this.repository.findAll()).thenReturn(agendas);
        List<Agenda> result = this.service.readAll();

        Mockito.verify(this.repository, Mockito.times(1)).findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(agendas.size());
        Assertions.assertThat(result.getFirst().getId()).isEqualTo(agendas.getFirst().getId());
        Assertions.assertThat(result.getLast().getId()).isEqualTo(agendas.getLast().getId());
    }
}

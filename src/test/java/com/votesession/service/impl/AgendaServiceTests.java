package com.votesession.service.impl;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.votesession.domain.Agenda;
import com.votesession.repository.AgendaRepository;
import com.votesession.service.contracts.AgendaService;

@SpringBootTest
public class AgendaServiceTests {
    @Autowired
    AgendaService service;

    @MockBean
    AgendaRepository repository;

    @Test
    @DisplayName("Should create new agenda")
    void shouldCreateNewAgenda() {
        Agenda agenda = Agenda
                .builder()
                .title("Any tittle")
                .description("Any_Description")
                .active(true)
                .build();

        Agenda savedAgenda = agenda;
        savedAgenda.setId(1L);
        savedAgenda.setActive(true);
        savedAgenda.setCreatedAt(LocalDateTime.now());
        savedAgenda.setUpdatedAt(LocalDateTime.now());

        Mockito.when(this.repository.save(agenda)).thenReturn(savedAgenda);

        Agenda result = this.service.create(agenda);

        Mockito.verify(this.repository, Mockito.times(1)).save(agenda);
        Assertions.assertThat(result.getId()).isEqualTo(savedAgenda.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(savedAgenda.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(savedAgenda.getDescription());
        Assertions.assertThat(result.isActive()).isEqualTo(savedAgenda.isActive());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(savedAgenda.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(savedAgenda.getUpdatedAt());
    }
}

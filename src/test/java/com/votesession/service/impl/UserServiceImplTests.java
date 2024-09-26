package com.votesession.service.impl;

import com.votesession.domain.model.InverterTextoApiResponse;
import com.votesession.mocks.MocksFactory;
import com.votesession.service.contracts.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class UserServiceImplTests {

    @Autowired
    private UserService service;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    @DisplayName("Should return false if user document is invalid")
    void shouldReturnFalseIfUserDocumentIsInvalid() {
        WebClient webClient = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(webClient.get()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        Mockito.when(uriSpec.retrieve()).thenReturn(responseSpec);

        InverterTextoApiResponse fakeResponse = new InverterTextoApiResponse(false, MocksFactory.faker.lorem().word());
        Mockito.when(responseSpec.bodyToMono(InverterTextoApiResponse.class)).thenReturn(Mono.just(fakeResponse));


        boolean result = this.service.isAbleToVote("12345678909");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true if user document is valid")
    void shouldReturnTruIfUserDocumentIsValid() {
        WebClient webClient = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(webClientBuilder.build()).thenReturn(webClient);
        Mockito.when(webClient.get()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        Mockito.when(uriSpec.retrieve()).thenReturn(responseSpec);

        InverterTextoApiResponse fakeResponse = new InverterTextoApiResponse(true, MocksFactory.faker.lorem().word());
        Mockito.when(responseSpec.bodyToMono(InverterTextoApiResponse.class)).thenReturn(Mono.just(fakeResponse));


        boolean result = this.service.isAbleToVote("12345678909");

        assertTrue(result);
    }
}

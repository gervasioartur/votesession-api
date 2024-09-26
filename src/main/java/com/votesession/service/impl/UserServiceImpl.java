package com.votesession.service.impl;

import com.votesession.domain.model.InverterTextoApiResponse;
import com.votesession.service.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * This class deals with document validation,
 * it uses inverter texto api that can be accessed by thi link https://api.invertexto.com/.
 * This api just validates if the cpf is valid or not.
 * To call the api it uses webclient.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final WebClient.Builder webClientBuilder;
    @Value("${inverter.texto.api.token}")
    private String inverterTextoApiToken;

    @Override
    public boolean isAbleToVote(String document) {
        final String URI = "https://api.invertexto.com/v1/validator"
                + "?token=" + this.inverterTextoApiToken
                + "&value=" + document
                + "&type=cpf";

        //Getting the result using wecClient
        InverterTextoApiResponse response = this.webClientBuilder.build()
                .get()
                .uri(URI)
                .retrieve()
                .bodyToMono(InverterTextoApiResponse.class).block();
        return response.isValid();
    }
}

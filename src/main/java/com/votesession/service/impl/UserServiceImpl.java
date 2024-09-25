package com.votesession.service.impl;

import com.votesession.service.contracts.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean isAbleToVote(String document) {
        return false;
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

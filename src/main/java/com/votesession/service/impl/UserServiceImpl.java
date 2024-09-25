package com.votesession.service.impl;

import com.votesession.service.contracts.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean isAbleToVote(String document) {
        return false;
    }
}

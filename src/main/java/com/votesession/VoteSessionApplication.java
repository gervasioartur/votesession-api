package com.votesession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class VoteSessionApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoteSessionApplication.class, args);
    }

}

package com.votesession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VoteSessionApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }

    @Test
    void mainMethodRunsSuccessfully() {
        VoteSessionApplication.main(new String[]{});
        Assertions.assertTrue(true);
    }
}

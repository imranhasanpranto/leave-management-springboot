package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.model.UserLeaveCount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserLeaveCountRepositoryTest {
    @Autowired
    UserLeaveCountRepository userLeaveCountRepository;
    @BeforeEach
    void setUp() {
        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(100l)
                .year(2023)
                .value(23)
                .build();
        userLeaveCountRepository.save(userLeaveCount);
    }

    @AfterEach
    void tearDown() {
        userLeaveCountRepository.deleteAll();
    }

    @Test
    void findByUserIdAndYear() {
        Optional<UserLeaveCount> userLeaveCountOptional = userLeaveCountRepository.findByUserIdAndYear(100l, 2023);
        assertTrue(userLeaveCountOptional.isPresent());
    }
}
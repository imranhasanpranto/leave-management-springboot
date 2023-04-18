package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.dto.LeaveDaysDTO;
import com.enosis.leavemanagement.model.UserLeaveDays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.xml.crypto.Data;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserLeaveDaysRepositoryTest {
    @Autowired
    UserLeaveDaysRepository userLeaveDaysRepository;
    @BeforeEach
    void setUp() {
        UserLeaveDays userLeaveDays = UserLeaveDays.builder()
                .id(1l)
                .leaveApplicationId(100l)
                .leaveDate(LocalDate.now())
                .build();
        userLeaveDaysRepository.save(userLeaveDays);
    }

    @AfterEach
    void tearDown() {
        userLeaveDaysRepository.deleteAll();
    }

    @Test
    void findByLeaveApplicationId() {
        List<UserLeaveDays> userLeaveDaysList = userLeaveDaysRepository.findByLeaveApplicationId(100l);
        List<UserLeaveDays> expectedResult = new ArrayList<>();
        UserLeaveDays userLeaveDays = UserLeaveDays.builder()
                .id(1l)
                .leaveApplicationId(100l)
                .leaveDate(LocalDate.now())
                .build();
        expectedResult.add(userLeaveDays);

        assertIterableEquals(expectedResult, userLeaveDaysList);
        //assertArrayEquals(expectedResult.toArray(), userLeaveDaysList.toArray());
    }
}
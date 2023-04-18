package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.repository.UserLeaveCountRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLeaveCountServiceTest {
    static final String ANNUAL_LEAVE_COUNT = "leave-count";

    @Mock
    UserLeaveCountRepository repository;
    @Mock
    GlobalConfigService gService;

    @InjectMocks
    UserLeaveCountService service;

    @Test
    void getLeaveBalance() {
        long userId = 100l;
        int year = 2023;
        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(userId)
                .year(year)
                .value(100)
                .build();

        when(repository.findByUserIdAndYear(userId, year)).thenReturn(Optional.of(userLeaveCount));
        UserLeaveCount actual = service.getLeaveBalance(userId);
        assertEquals(userLeaveCount, actual);

        //entry does not exist on db
        UserLeaveCountService userLeaveCountService = new UserLeaveCountService(repository, gService);
        UserLeaveCountService spyService = spy(userLeaveCountService);

        when(repository.findByUserIdAndYear(userId, year)).thenReturn(Optional.empty());
        doReturn(userLeaveCount).when(spyService).setUserAnnualLeaveCount(userId, year);

        UserLeaveCount actual2 = spyService.getLeaveBalance(userId);
        assertEquals(userLeaveCount, actual2);
    }

    @Test
    void updateLeaveCountBalance() {
        long userId = 1l;
        int balance = 24;
        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(userId)
                .year(2023)
                .value(22)
                .build();

        UserLeaveCountService userLeaveCountService = new UserLeaveCountService(repository, gService);
        UserLeaveCountService spyService = spy(userLeaveCountService);

        doReturn(userLeaveCount).when(spyService).getLeaveBalance(userId);

        UserLeaveCount userLeaveCountActual = spyService.updateLeaveCountBalance(userId, balance);
        assertEquals(balance, userLeaveCountActual.getValue());
    }

    @Test
    void setUserAnnualLeaveCount() {
        int configValue = 100;
        long userId = 1l;
        int year = 2023;
        GlobalConfig config = GlobalConfig.builder()
                .id(1l)
                .configName(ANNUAL_LEAVE_COUNT)
                .configValue(configValue)
                .build();

        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(userId)
                .year(year)
                .value(configValue)
                .build();

        when(gService.findByName(ANNUAL_LEAVE_COUNT)).thenReturn(config);
        when(repository.save(userLeaveCount)).thenReturn(userLeaveCount);

        UserLeaveCount userLeaveCountActual = service.setUserAnnualLeaveCount(userId, year);
        assertEquals(userLeaveCount, userLeaveCountActual);
    }
}
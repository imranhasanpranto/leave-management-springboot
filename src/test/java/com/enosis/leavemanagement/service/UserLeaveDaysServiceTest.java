package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.repository.UserLeaveDaysRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLeaveDaysServiceTest {

    @Mock
    UserLeaveDaysRepository repository;

    @InjectMocks
    UserLeaveDaysService service;

    @Test
    void deleteByIds() {
        List<Long> ids = List.of(10l,11l,12l);
        repository.deleteByIdIn(ids);
        verify(repository).deleteByIdIn(ids);
    }

    @Test
    void deleteByApplicationId() {
        long appId = 100l;
        List<LocalDate> list = new ArrayList<>();
        list.add(LocalDate.of(2023, 12,30));
        list.add(LocalDate.of(2023, 11,30));

        List<UserLeaveDays> leaveDaysList = new ArrayList<>();
        leaveDaysList.add(UserLeaveDays.builder().id(1l).leaveApplicationId(appId).leaveDate(list.get(0)).build());
        leaveDaysList.add(UserLeaveDays.builder().id(2l).leaveApplicationId(appId).leaveDate(list.get(1)).build());


        UserLeaveDaysService tempService = new UserLeaveDaysService(repository);
        UserLeaveDaysService spyService = spy(tempService);

        doReturn(leaveDaysList).when(spyService).findByApplicationId(appId);

        List<Long> ids = List.of(1l, 2l);

        spyService.deleteByApplicationId(appId);
        verify(spyService).deleteByIds(ids);
    }

    @Test
    void findByApplicationId() {
        long leaveApplicationId = 2l;
        List<UserLeaveDays> list = new ArrayList<>();
        list.add(new UserLeaveDays(1l, leaveApplicationId, LocalDate.of(2023, 12,30)));
        list.add(new UserLeaveDays(2l, leaveApplicationId, LocalDate.of(2023, 11,30)));

        when(repository.findByLeaveApplicationId(leaveApplicationId)).thenReturn(list);
        List<UserLeaveDays> actualList = service.findByApplicationId(leaveApplicationId);
        assertEquals(list.size(), actualList.size());
        assertIterableEquals(list, actualList);
    }


    @Test
    void saveAllByDateList() {
        List<LocalDate> list = new ArrayList<>();
        long appId = 100l;
        list.add(LocalDate.of(2023, 12,30));
        list.add(LocalDate.of(2023, 11,30));

        List<UserLeaveDays> leaveDaysList = new ArrayList<>();
        leaveDaysList.add(UserLeaveDays.builder().leaveApplicationId(appId).leaveDate(list.get(0)).build());
        leaveDaysList.add(UserLeaveDays.builder().leaveApplicationId(appId).leaveDate(list.get(1)).build());

        UserLeaveDaysService tempService = new UserLeaveDaysService(repository);
        UserLeaveDaysService spyService = spy(tempService);

        spyService.saveAllByDateList(list, 100l);
        verify(spyService).saveAll(leaveDaysList);
    }

    @Test
    void saveAll() {
        List<UserLeaveDays> list = new ArrayList<>();
        list.add(new UserLeaveDays(1l, 1l, LocalDate.of(2023, 12,30)));
        list.add(new UserLeaveDays(2l, 2l, LocalDate.of(2023, 11,30)));

        service.saveAll(list);
        verify(repository).saveAll(list);
    }
}
package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.service.UserLeaveDaysService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLeaveDaysRestControllerTest {
    @Mock
    UserLeaveDaysService service;

    @InjectMocks
    UserLeaveDaysRestController controller;

    @Test
    void getUserCurrentLeaveBalance() {
        long leaveApplicationId = 2l;
        List<UserLeaveDays> list = new ArrayList<>();
        list.add(new UserLeaveDays(1l, leaveApplicationId, LocalDate.of(2023, 12,30)));
        list.add(new UserLeaveDays(2l, leaveApplicationId, LocalDate.of(2023, 11,30)));

        when(service.findByApplicationId(leaveApplicationId)).thenReturn(list);

        ResponseEntity<List<UserLeaveDays>> response = controller.getUserCurrentLeaveBalance(leaveApplicationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertIterableEquals(list, response.getBody());
    }
}
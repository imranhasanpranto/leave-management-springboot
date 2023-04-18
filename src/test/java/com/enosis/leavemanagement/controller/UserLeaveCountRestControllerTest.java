package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserLeaveCountRepository;
import com.enosis.leavemanagement.service.UserLeaveCountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLeaveCountRestControllerTest {
    @Mock
    UserLeaveCountService service;

    @InjectMocks
    UserLeaveCountRestController controller;

    @Test
    void getUserCurrentLeaveBalance() {
        Users users = Users.builder()
                .id(1l)
                .name("xyz")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Employee)
                .build();

        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .id(1l)
                .year(2023)
                .userId(1l)
                .value(22)
                .build();

        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn(users);
        when(service.getLeaveBalance(users.getId())).thenReturn(userLeaveCount);

        ResponseEntity<UserLeaveCount> response = controller.getUserCurrentLeaveBalance(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userLeaveCount, response.getBody());
    }
}
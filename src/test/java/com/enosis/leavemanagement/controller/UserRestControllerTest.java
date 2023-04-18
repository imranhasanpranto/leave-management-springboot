package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;
    @Test
    void isUserEmailTaken() {
        String email = "x@gmail.com";
        Users users = Users.builder()
                        .name("Imran")
                        .email(email)
                        .build();
        when(userService.findByEmail(email)).thenReturn(Optional.of(users));
        ResponseEntity<RestResponse> response = userRestController.isUserEmailTaken(email);

        assertTrue(response.getBody().isStatus());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
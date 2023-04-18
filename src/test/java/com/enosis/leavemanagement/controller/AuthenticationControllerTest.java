package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.AuthenticationRequest;
import com.enosis.leavemanagement.dto.AuthenticationResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {


    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("Imran")
                .email("xy@gmail.com")
                .password("12345")
                .build();

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token("xyz")
                .build();


        when(authenticationService.register(userDTO)).thenReturn(authenticationResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.register(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("xyz", response.getBody().getToken());
    }

    @Test
    void authenticate() {

        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("x@gmail.com")
                .password("12345")
                .build();

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token("xyz")
                .build();

        when(authenticationService.authenticate(authenticationRequest)).thenReturn(authenticationResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(authenticationRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("xyz", response.getBody().getToken());
    }
}
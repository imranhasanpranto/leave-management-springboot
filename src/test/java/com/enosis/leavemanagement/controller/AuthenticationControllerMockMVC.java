package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.AuthenticationRequest;
import com.enosis.leavemanagement.dto.AuthenticationResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {AuthenticationControllerMockMVC.class})
public class AuthenticationControllerMockMVC {
    @Autowired
    MockMvc mockMvc;

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    AuthenticationController authenticationController;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
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

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/auth/register")
                .content(mapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(".token").value("xyz"));

    }

    @Test
    void authenticate() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("x@gmail.com")
                .password("12345")
                .build();

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token("xyz")
                .build();


        when(authenticationService.authenticate(authenticationRequest)).thenReturn(authenticationResponse);

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/auth/authenticate")
                        .content(mapper.writeValueAsString(authenticationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(".token").value("xyz"));
    }

    @Test
    void testAlreadyExistEmailException(){

    }

}

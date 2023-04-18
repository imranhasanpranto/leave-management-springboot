package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {UserRestControllerMockMVC.class})
public class UserRestControllerMockMVC {
    @Autowired
    MockMvc mockMvc;

    @Mock
    UserService userService;

    @InjectMocks
    UserRestController userRestController;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    void isUserEmailTaken() throws Exception {
        String email = "x@gmail.com";
        Users users = Users.builder()
                .name("Imran")
                .email(email)
                .build();
        when(userService.findByEmail(email)).thenReturn(Optional.of(users));

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(get("/api/user/isUserEmailTaken/{userEmail}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)));
    }
}

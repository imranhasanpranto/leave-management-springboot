package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.service.ApiExceptionHandler;
import com.enosis.leavemanagement.service.UserLeaveDaysService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {UserLeaveDaysRestControllerMocMVCTest.class})
public class UserLeaveDaysRestControllerMocMVCTest {
    @Autowired
    MockMvc mockMvc;

    @Mock
    UserLeaveDaysService service;

    @InjectMocks
    UserLeaveDaysRestController controller;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getUserCurrentLeaveBalanceNotFoundTest() throws Exception {
        Long id = 100L;
        this.mockMvc.perform(get("/api/leave-days/getLeaveDays/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));

    }

}

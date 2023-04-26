package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.service.ApiExceptionHandler;
import com.enosis.leavemanagement.service.GlobalConfigService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {GlobalConfigControllerMockMVC.class})
public class GlobalConfigControllerMockMVC {
    @Autowired
    MockMvc mockMvc;

    @Mock
    GlobalConfigService service;

    @InjectMocks
    GlobalConfigController controller;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getByNameNotFoundExceptionTest() throws Exception {
        String name = "config";

        when(service.findByName(name)).thenReturn(null);
        this.mockMvc.perform(get("/api/config/getByName/{name}", name))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals(name+" Not Found.", result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser
    void addConfigAlreadyExistExceptionTest() throws Exception {
        GlobalConfig config = GlobalConfig.builder()
                        .configName("name")
                        .configValue(100)
                        .build();
        when(service.addConfig(config, Role.Admin)).thenThrow(AlreadyExistsException.class);
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(post("/api/config/add")
                        .content(mapper.writeValueAsString(config))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(config.getConfigName()+" Already Exists.", result.getResolvedException().getMessage()));
    }

}

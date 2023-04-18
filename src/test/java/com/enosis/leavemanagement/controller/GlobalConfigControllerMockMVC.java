package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.GlobalConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {AuthenticationControllerMockMVC.class})
class GlobalConfigControllerMockMVC {

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;
    @Mock
    GlobalConfigService service;

    @InjectMocks
    GlobalConfigController configController;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser("spring")
    @Test
    void updateConfig() throws Exception {
        GlobalConfig config = GlobalConfig.builder()
                .id(1l)
                .configName("config")
                .configValue(22)
                .build();

        Users users = Users.builder()
                .id(1l)
                .name("xyz")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Employee)
                .build();

        Authentication auth = mock(Authentication.class);
        when(service.updateConfig(config, Role.Employee)).thenReturn(config);
        when(auth.getPrincipal()).thenReturn(users);
        ObjectMapper mapper = new ObjectMapper();
        String res = this.mockMvc.perform(put("/api/config/update")
                //.with(authentication(auth))
                .content(mapper.writeValueAsString(config))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponse response = mapper.readValue(res, RestResponse.class);
        assertTrue(response.isStatus());
    }

    @Test
    @Disabled
    void addConfig() {
        GlobalConfig config = GlobalConfig.builder()
                .id(1l)
                .configName("config")
                .configValue(22)
                .build();

        Users users = Users.builder()
                .id(1l)
                .name("xyz")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Employee)
                .build();

        Authentication auth = mock(Authentication.class);
        when(service.addConfig(config, Role.Employee)).thenReturn(config);
        when(auth.getPrincipal()).thenReturn(users);
        ResponseEntity<RestResponse> response = configController.addConfig(config, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isStatus());
    }

    @Test
    @Disabled
    void getByName() {
        String name = "config";
        GlobalConfig config = GlobalConfig.builder()
                .id(1l)
                .configName(name)
                .configValue(22)
                .build();

        when(service.findByName(name)).thenReturn(config);
        ResponseEntity<GlobalConfig> response = configController.getByName(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(config, response.getBody());
    }
}
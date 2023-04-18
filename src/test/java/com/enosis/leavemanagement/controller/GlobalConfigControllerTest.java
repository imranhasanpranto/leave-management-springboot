package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.GlobalConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class GlobalConfigControllerTest {
    @Mock
    GlobalConfigService service;

    @InjectMocks
    GlobalConfigController configController;

    @Test
    void updateConfig() {
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
        ResponseEntity<RestResponse> response = configController.updateConfig(config, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isStatus());
    }

    @Test
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
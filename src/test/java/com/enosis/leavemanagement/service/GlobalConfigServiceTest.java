package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.repository.GlobalConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalConfigServiceTest {

    @Mock
    GlobalConfigRepository repository;
    @InjectMocks
    GlobalConfigService service;

    @Test
    void findByName() {
        String configName = "config";
        GlobalConfig expected = GlobalConfig.builder()
                        .id(1l)
                        .configName(configName)
                        .configValue(2)
                        .build();
        when(repository.findByConfigName(configName)).thenReturn(Optional.of(expected));
        GlobalConfig config = service.findByName(configName);
        assertEquals(expected, config);

        //config does not exist check
        when(repository.findByConfigName(configName)).thenReturn(Optional.empty());
        GlobalConfig configNull = service.findByName(configName);
        assertNull(configNull);
    }

    @Test
    void addConfig() {
        String configName = "config";
        GlobalConfig expected = GlobalConfig.builder()
                .configName(configName)
                .configValue(2)
                .build();


        GlobalConfigService spyService = new GlobalConfigService(repository);
        GlobalConfigService globalConfigService = spy(spyService);
        doReturn(null).when(globalConfigService).findByName(configName);
        when(repository.save(expected)).thenReturn(expected);

        GlobalConfig config = globalConfigService.addConfig(expected, Role.Admin);
        assertEquals(expected, config);
    }

    @Test
    void updateConfig() {
        String configName = "config";
        GlobalConfig oldConfig = GlobalConfig.builder()
                .id(1l)
                .configName(configName)
                .configValue(2)
                .build();

        GlobalConfig newConfig = GlobalConfig.builder()
                .id(1l)
                .configName(configName)
                .configValue(3)
                .build();

        GlobalConfigService spyService = new GlobalConfigService(repository);
        GlobalConfigService globalConfigService = spy(spyService);
        doReturn(oldConfig).when(globalConfigService).findByName(configName);

        GlobalConfig config = globalConfigService.updateConfig(newConfig, Role.Admin);
        assertEquals(newConfig, config);
    }

}
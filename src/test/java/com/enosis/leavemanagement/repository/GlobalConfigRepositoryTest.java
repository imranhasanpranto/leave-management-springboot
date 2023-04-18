package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.model.GlobalConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GlobalConfigRepositoryTest {

    @Autowired
    private GlobalConfigRepository globalConfigRepository;
    @BeforeEach
    void setUp() {
        GlobalConfig globalConfig = GlobalConfig.builder()
                .configName("config")
                .configValue(100)
                .build();
        globalConfigRepository.save(globalConfig);
    }

    @AfterEach
    void tearDown() {
        globalConfigRepository.deleteAll();
    }

    @Test
    void findByConfigNameExists() {
        Optional<GlobalConfig> globalConfigOptional = globalConfigRepository.findByConfigName("config");
        assertTrue(globalConfigOptional.isPresent());
    }

    @Test
    void findByConfigNameNotExists(){
        Optional<GlobalConfig> globalConfigOptional = globalConfigRepository.findByConfigName("configTest");
        assertFalse(globalConfigOptional.isPresent());
    }
}
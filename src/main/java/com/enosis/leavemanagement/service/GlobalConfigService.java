package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.repository.GlobalConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GlobalConfigService {
    private final GlobalConfigRepository globalConfigRepository;

    public GlobalConfig findByName(String name) {
        Optional<GlobalConfig> globalConfigOptional = globalConfigRepository.findByConfigName(name);
        if(globalConfigOptional.isPresent()){
            return globalConfigOptional.get();
        }

        return null;
    }
    @Transactional
    public String updateConfig(GlobalConfig globalConfig, Role role){
        if(role.equals(Role.Admin)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            if(config == null){
                return "config not found!!!";
            }else{
                config.setConfigValue(globalConfig.getConfigValue());
                return "updated successfully";
            }
        }
        return "Unauthorized!!!";
    }

    @Transactional
    public String addConfig(GlobalConfig globalConfig, Role role){
        if(role.equals(Role.Admin)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            if(config == null){
                config = GlobalConfig.builder()
                        .configName(globalConfig.getConfigName())
                        .configValue(globalConfig.getConfigValue())
                        .build();
                globalConfigRepository.save(config);

                return "saved successfully";
            }else{
                return "duplicate config!!!";
            }
        }
        return "Unauthorized!!!";
    }
}

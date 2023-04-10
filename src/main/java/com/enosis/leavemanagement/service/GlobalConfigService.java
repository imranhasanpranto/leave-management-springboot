package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.exceptions.UnAuthorizedAccessException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.repository.GlobalConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GlobalConfigService {
    private final GlobalConfigRepository globalConfigRepository;
    static final String ANNUAL_LEAVE_COUNT = "leave-count";

    public GlobalConfig findByName(String name) {
        Optional<GlobalConfig> globalConfigOptional = globalConfigRepository.findByConfigName(name);
        if(globalConfigOptional.isPresent()){
            return globalConfigOptional.get();
        }
        return null;
    }
    @Transactional
    public String updateConfig(GlobalConfig globalConfig, Role role) throws NotFoundException, UnAuthorizedAccessException {
        if(role.equals(Role.Admin)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            if(config == null){
                throw new NotFoundException(globalConfig.getConfigName()+" Not Found.");
            }
            config.setConfigValue(globalConfig.getConfigValue());
            return "updated successfully";

        }else{
            throw new UnAuthorizedAccessException("UnAuthorized Access");
        }
    }

    @Transactional
    public String addConfig(GlobalConfig globalConfig, Role role) throws AlreadyExistsException, UnAuthorizedAccessException {
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
                throw new AlreadyExistsException(globalConfig.getConfigName()+" Already Exists.");
            }
        }else{
            throw new UnAuthorizedAccessException("UnAuthorized Access");
        }
    }
}

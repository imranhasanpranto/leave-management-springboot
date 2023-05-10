package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.exceptions.UnAuthorizedAccessException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.repository.GlobalConfigRepository;
import com.enosis.leavemanagement.repository.UserLeaveCountRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GlobalConfigService {
    private final GlobalConfigRepository globalConfigRepository;
    private final UserLeaveCountRepository leaveCountRepository;
    static final String ANNUAL_LEAVE_COUNT = "leave-count";

    public GlobalConfig findByName(String name) {
        Optional<GlobalConfig> globalConfigOptional = globalConfigRepository.findByConfigName(name);
        if(globalConfigOptional.isPresent()){
            return globalConfigOptional.get();
        }
        return null;
    }
    @Transactional
    public GlobalConfig updateConfig(GlobalConfig globalConfig, Role role) throws NotFoundException, UnAuthorizedAccessException {
        if(role.equals(Role.Admin)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            int configPrevValue = config.getConfigValue();
            if(config == null){
                throw new NotFoundException(globalConfig.getConfigName()+" Not Found.");
            }
            config.setConfigValue(globalConfig.getConfigValue());
            if(config.getConfigName().equals(ANNUAL_LEAVE_COUNT)){
                updateUserLeaveCount(globalConfig.getConfigValue(), configPrevValue);
            }
            return config;

        }else{
            throw new UnAuthorizedAccessException("UnAuthorized Access");
        }
    }

    @Transactional
    public void updateUserLeaveCount(int configCurrentValue, int configPrevValue){
        List<UserLeaveCount> list = leaveCountRepository.findAll();
        int diff = configCurrentValue - configPrevValue;
        for(UserLeaveCount ob: list){
            ob.setValue(ob.getValue()+diff);
        }
    }

    @Transactional
    public GlobalConfig addConfig(GlobalConfig globalConfig, Role role) throws AlreadyExistsException, UnAuthorizedAccessException {
        if(role.equals(Role.Admin)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            if(config == null){
                config = GlobalConfig.builder()
                        .configName(globalConfig.getConfigName())
                        .configValue(globalConfig.getConfigValue())
                        .build();
                config = globalConfigRepository.save(config);

                return config;
            }else{
                throw new AlreadyExistsException(globalConfig.getConfigName()+" Already Exists.");
            }
        }else{
            throw new UnAuthorizedAccessException("UnAuthorized Access");
        }
    }
}

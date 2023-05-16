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
        throw new NotFoundException(
                name+" Not Found."
        );
    }
    @Transactional
    public GlobalConfig updateConfig(GlobalConfig globalConfig, Role role){
        if(Role.Admin.equals(role)){
            GlobalConfig config = findByName(globalConfig.getConfigName());
            int configPrevValue = config.getConfigValue();
            config.setConfigValue(globalConfig.getConfigValue());
            if(ANNUAL_LEAVE_COUNT.equals(config.getConfigName())){
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
    public GlobalConfig addConfig(GlobalConfig globalConfig, Role role){
        if(Role.Admin.equals(role)){
            try{
                findByName(globalConfig.getConfigName());
            }catch(NotFoundException e){
                GlobalConfig config = GlobalConfig.builder()
                        .configName(globalConfig.getConfigName())
                        .configValue(globalConfig.getConfigValue())
                        .build();
                config = globalConfigRepository.save(config);

                return config;
            }
            throw new AlreadyExistsException(globalConfig.getConfigName()+" Already Exists.");
        }else{
            throw new UnAuthorizedAccessException("UnAuthorized Access");
        }
    }
}

package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.repository.UserLeaveCountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.Optional;

import static com.enosis.leavemanagement.service.GlobalConfigService.ANNUAL_LEAVE_COUNT;

@Service
@RequiredArgsConstructor
public class UserLeaveCountService {
    private final UserLeaveCountRepository userLeaveCountRepository;
    private final GlobalConfigService globalConfigService;

    public UserLeaveCount getLeaveBalance(Long userId) throws NotFoundException {
        int year = Year.now().getValue();

        Optional<UserLeaveCount> userLeaveCountOptional = userLeaveCountRepository.findByUserIdAndYear(userId, year);
        if(userLeaveCountOptional.isPresent()){
            return userLeaveCountOptional.get();
        }else{
            return setUserAnnualLeaveCount(userId, year);
        }
    }

    @Transactional
    public UserLeaveCount updateLeaveCountBalance(Long userId, int balance) throws NotFoundException{
        UserLeaveCount leaveCount = getLeaveBalance(userId);
        leaveCount.setValue(balance);
        return leaveCount;
    }

    public UserLeaveCount setUserAnnualLeaveCount(Long userId, int year) throws NotFoundException {
        GlobalConfig globalConfig = globalConfigService.findByName(ANNUAL_LEAVE_COUNT);
        if(globalConfig == null){
            throw new NotFoundException(ANNUAL_LEAVE_COUNT+" Not Found.");
        }
        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(userId)
                .year(year)
                .value(globalConfig.getConfigValue())
                .build();

        return userLeaveCountRepository.save(userLeaveCount);
    }
}

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

    public UserLeaveCount getLeaveBalance(Long userId) {
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

    @Transactional
    public void updateLeaveCountAfterDeleteApplication(Long userId, int count){
        UserLeaveCount currentBalance = getLeaveBalance(userId);
        int balance = currentBalance.getValue()+count;
        updateLeaveCountBalance(userId, balance);
    }

    public UserLeaveCount setUserAnnualLeaveCount(Long userId, int year) {
        GlobalConfig globalConfig = globalConfigService.findByName(ANNUAL_LEAVE_COUNT);

        UserLeaveCount userLeaveCount = UserLeaveCount.builder()
                .userId(userId)
                .year(year)
                .value(globalConfig.getConfigValue())
                .build();

        return userLeaveCountRepository.save(userLeaveCount);
    }
}

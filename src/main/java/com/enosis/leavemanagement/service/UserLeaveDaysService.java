package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.repository.UserLeaveDaysRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLeaveDaysService {
    private final UserLeaveDaysRepository userLeaveDaysRepository;

    @Transactional
    public void deleteByIds(List<Long> ids){
        userLeaveDaysRepository.deleteByIdIn(ids);
    }

    @Transactional
    public void deleteByApplicationId(Long applicationId){
        List<UserLeaveDays> userLeaveDaysList = findByApplicationId(applicationId);
        List<Long> ids = userLeaveDaysList.stream()
                .map(ob -> ob.getId())
                .collect(Collectors.toList());

        deleteByIds(ids);
    }

    public List<UserLeaveDays> findByApplicationId(Long applicationId){
        return userLeaveDaysRepository.findByLeaveApplicationId(applicationId);
    }

    public UserLeaveDays createLeaveDaysEntity(LocalDate localDate, Long applicationId){
        UserLeaveDays userLeaveDays = UserLeaveDays.builder()
                .leaveApplicationId(applicationId)
                .leaveDate(localDate)
                .build();

        return userLeaveDays;
    }

    @Transactional
    public void saveAllByDateList(List<LocalDate> localDateList, final Long applicationId){
        List<UserLeaveDays> leaveDaysList = localDateList.stream()
                .map(localDate -> createLeaveDaysEntity(localDate, applicationId))
                .collect(Collectors.toList());

        saveAll(leaveDaysList);
    }

    @Transactional
    public void saveAll(List<UserLeaveDays> userLeaveDays){
        userLeaveDaysRepository.saveAll(userLeaveDays);
    }
}

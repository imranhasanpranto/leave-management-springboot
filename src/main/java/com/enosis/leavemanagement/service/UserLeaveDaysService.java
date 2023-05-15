package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.repository.UserLeaveDaysRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
                .toList();

        deleteByIds(ids);
    }

    public List<UserLeaveDays> findByApplicationId(Long applicationId){
        return userLeaveDaysRepository.findByLeaveApplicationId(applicationId);
    }

    public UserLeaveDays createLeaveDaysEntity(LocalDate localDate, Long applicationId){
        return UserLeaveDays.builder()
                .leaveApplicationId(applicationId)
                .leaveDate(localDate)
                .build();
    }

    @Transactional
    public void saveAllByDateList(List<LocalDate> localDateList, final Long applicationId){
        List<UserLeaveDays> leaveDaysList = localDateList.stream()
                .map(localDate -> createLeaveDaysEntity(localDate, applicationId))
                .toList();

        saveAll(leaveDaysList);
    }

    @Transactional
    public void saveAll(List<UserLeaveDays> userLeaveDays){
        userLeaveDaysRepository.saveAll(userLeaveDays);
    }
}

package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.model.UserLeaveDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLeaveDaysRepository extends JpaRepository<UserLeaveDays, Long> {
    void deleteByIdIn(List<Long> ids);

    List<UserLeaveDays> findByLeaveApplicationId(Long applicationId);
}

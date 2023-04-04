package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {
    public List<LeaveApplication> findByApplicationStatusOrderByIdDesc(ApplicationStatus pending);
    public List<LeaveApplication> findByUserIdAndApplicationStatusOrderByIdDesc(Long userId, ApplicationStatus pending);
}

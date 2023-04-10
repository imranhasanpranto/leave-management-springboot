package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.interfaces.ProjectDateRange;
import com.enosis.leavemanagement.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {
    final String baseSqlQuery = "SELECT new com.enosis.leavemanagement.dto.UserLeaveApplicationDTO(app.id, app.userId, users.name, " +
            " app.fromDate, app.toDate, app.leaveReason, app.emergencyContact, " +
            " app.leaveType, app.filePath, app.applicationStatus) " +
            "FROM LeaveApplication app INNER JOIN Users users " +
            "ON app.userId = users.id ";

    @Query(value =  baseSqlQuery + " WHERE app.applicationStatus = ?1 ")
    public List<UserLeaveApplicationDTO> findByApplicationStatusOrderByIdDesc(ApplicationStatus pending);

    @Query(value =  baseSqlQuery + " WHERE users.id = ?1 AND app.applicationStatus = ?2 ")
    public List<UserLeaveApplicationDTO> findByUserIdAndApplicationStatusOrderByIdDesc(Long userId, ApplicationStatus pending);

    @Query(value =  baseSqlQuery + " WHERE users.name LIKE CONCAT('%', ?1, '%') AND app.applicationStatus = ?2 ")
    public List<UserLeaveApplicationDTO> findApprovedListByName(String name, ApplicationStatus status);

    public List<ProjectDateRange> findByUserIdAndIdNotAndApplicationStatusIn(Long userId, Long id, List<ApplicationStatus> statusList);
}

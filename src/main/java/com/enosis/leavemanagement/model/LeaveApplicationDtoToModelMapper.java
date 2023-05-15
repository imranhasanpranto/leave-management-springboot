package com.enosis.leavemanagement.model;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeaveApplicationDtoToModelMapper {
    public LeaveApplication mapDtoToModel(LeaveApplicationDTO leaveApplicationDTO){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LeaveApplication.builder()
                .fromDate(LocalDateTime.parse(leaveApplicationDTO.getFromDate(), formatter))
                .toDate(LocalDateTime.parse(leaveApplicationDTO.getToDate(), formatter))
                .leaveReason(leaveApplicationDTO.getLeaveReason())
                .leaveType(leaveApplicationDTO.getLeaveType())
                .applicationStatus(leaveApplicationDTO.getApplicationStatus())
                .emergencyContact(leaveApplicationDTO.getEmergencyContact())
                .build();
    }

    public void reMapDtoToModel(
            LeaveApplicationDTO leaveApplicationDTO,
            LeaveApplication leaveApplication,
            Integer leaveCount
    ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        leaveApplication.setFromDate(LocalDateTime.parse(leaveApplicationDTO.getFromDate(), formatter));
        leaveApplication.setToDate(LocalDateTime.parse(leaveApplicationDTO.getToDate(), formatter));
        leaveApplication.setLeaveReason(leaveApplicationDTO.getLeaveReason());
        leaveApplication.setLeaveType(leaveApplicationDTO.getLeaveType());
        leaveApplication.setEmergencyContact(leaveApplicationDTO.getEmergencyContact());
        leaveApplication.setLeaveCount(leaveCount);
    }
}

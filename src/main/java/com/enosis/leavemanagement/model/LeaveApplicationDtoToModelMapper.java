package com.enosis.leavemanagement.model;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;

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
}

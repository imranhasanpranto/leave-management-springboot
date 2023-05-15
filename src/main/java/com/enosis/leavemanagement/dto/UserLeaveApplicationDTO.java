package com.enosis.leavemanagement.dto;

import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaveApplicationDTO {
    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String leaveReason;
    private String emergencyContact;
    private LeaveType leaveType;
    private String filePath;
    private ApplicationStatus applicationStatus;

}

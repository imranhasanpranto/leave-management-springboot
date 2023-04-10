package com.enosis.leavemanagement.dto;

import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.LeaveType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeaveApplicationDTO {
    private Long id;
    private Long userId;
    private String fromDate;
    private String toDate;
    private LeaveType leaveType;
    private String leaveReason;
    private String emergencyContact;
    private MultipartFile file;
    private ApplicationStatus applicationStatus;
    private Boolean isFileUpdated;
}

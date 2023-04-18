package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.LeaveType;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class LeaveRepositoryTest {
    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    void setUp() {
        List<LeaveApplication> list = new ArrayList<>();
        LeaveApplication leaveApplication1 = LeaveApplication.builder()
                .applicationStatus(ApplicationStatus.Pending)
                .leaveType(LeaveType.Casual)
                .leaveReason("test")
                .toDate(LocalDateTime.of(2023, 4, 13, 0,0,0))
                .fromDate(LocalDateTime.of(2023, 4, 14, 0,0,0))
                .leaveCount(2)
                .emergencyContact("Imran")
                .filePath("")
                .userId(1l)
                .build();

        LeaveApplication leaveApplication2 = LeaveApplication.builder()
                .applicationStatus(ApplicationStatus.Approved)
                .leaveType(LeaveType.Sick)
                .leaveReason("test")
                .toDate(LocalDateTime.of(2023, 4, 13, 0,0,0))
                .fromDate(LocalDateTime.of(2023, 4, 14, 0,0,0))
                .leaveCount(2)
                .emergencyContact("Imran")
                .filePath("")
                .userId(1l)
                .build();

        list.add(leaveApplication1);
        list.add(leaveApplication2);
        leaveRepository.saveAll(list);

        String email = "test@gmail.com";
        Users users = Users.builder()
                .email(email)
                .name("Imran")
                .role(Role.Employee)
                .password("12345")
                .build();

        userRepository.save(users);
    }

    @AfterEach
    void tearDown() {
        leaveRepository.deleteAll();
    }

    @Test
    void findByApplicationStatusOrderByIdDesc() {
        List<UserLeaveApplicationDTO> list = leaveRepository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Approved);

        UserLeaveApplicationDTO dto = new UserLeaveApplicationDTO();
        dto.setId(2l);
        dto.setApplicationStatus(ApplicationStatus.Approved);
        dto.setLeaveReason("test");
        dto.setLeaveType(LeaveType.Sick);
        dto.setToDate(LocalDateTime.of(2023, 4, 13, 0,0,0));
        dto.setFromDate(LocalDateTime.of(2023, 4, 14, 0,0,0));
        dto.setUserId(1l);
        dto.setFilePath("");
        dto.setEmergencyContact("Imran");
        dto.setUserName("Imran");

        List<UserLeaveApplicationDTO> expectedList = new ArrayList<>();
        expectedList.add(dto);

        assertIterableEquals(expectedList, list);
    }
}
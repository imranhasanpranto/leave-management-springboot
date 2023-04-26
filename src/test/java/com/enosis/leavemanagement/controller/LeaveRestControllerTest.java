package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.LeaveType;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.LeaveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveRestControllerTest {
    @Mock
    LeaveService service;

    @InjectMocks
    LeaveRestController controller;

    Users users;
    LeaveApplicationDTO leaveApplicationDTO;
    List<UserLeaveApplicationDTO> approvedDTOList;
    List<UserLeaveApplicationDTO> pendingDTOList;

    LeaveApplication leaveApplication;

    @BeforeEach
    void setUp() {
        users = Users.builder()
                .id(1l)
                .name("xyz")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Employee)
                .build();

        leaveApplicationDTO = LeaveApplicationDTO.builder()
                .userId(1l)
                .applicationStatus(ApplicationStatus.Pending)
                .leaveType(LeaveType.Sick)
                .fromDate(LocalDateTime.of(2023, 4, 12, 0, 0, 0).toString())
                .toDate(LocalDateTime.of(2023, 4, 18, 0, 0, 0).toString())
                .build();

        leaveApplication = LeaveApplication.builder()
                .userId(1l)
                .applicationStatus(ApplicationStatus.Pending)
                .leaveType(LeaveType.Sick)
                .fromDate(LocalDateTime.of(2023, 4, 12, 0, 0, 0))
                .toDate(LocalDateTime.of(2023, 4, 18, 0, 0, 0))
                .build();

        approvedDTOList = new ArrayList<>();
        approvedDTOList.add(UserLeaveApplicationDTO.builder().id(1l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());
        approvedDTOList.add(UserLeaveApplicationDTO.builder().id(2l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());
        approvedDTOList.add(UserLeaveApplicationDTO.builder().id(3l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());

        pendingDTOList = new ArrayList<>();
        pendingDTOList.add(UserLeaveApplicationDTO.builder().id(1l).userName("imran").applicationStatus(ApplicationStatus.Pending).build());
        pendingDTOList.add(UserLeaveApplicationDTO.builder().id(2l).userName("imran").applicationStatus(ApplicationStatus.Pending).build());
        pendingDTOList.add(UserLeaveApplicationDTO.builder().id(3l).userName("imran").applicationStatus(ApplicationStatus.Pending).build());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addApplication() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(users);
        when(service.saveLeaveApplication(leaveApplicationDTO, 1l)).thenReturn("Leave request added successfully");

        ResponseEntity<RestResponse> response = controller.addApplication(leaveApplicationDTO, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Leave request added successfully", response.getBody().getMessage());
    }

    @Test
    void updateApplication() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(users);
        when(service.updateLeaveApplication(leaveApplicationDTO, 1l)).thenReturn("Leave request updated successfully");

        ResponseEntity<RestResponse> response = controller.updateApplication(leaveApplicationDTO, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Leave request updated successfully", response.getBody().getMessage());
    }

    @Test
    void getPendingList() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(users);
        when(service.getAllPendingLeaveRequests(users.getEmail())).thenReturn(pendingDTOList);

        ResponseEntity<List<UserLeaveApplicationDTO>> response = controller.getPendingList(auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pendingDTOList.size(), response.getBody().size());
    }

    @Test
    void getById() {
        Long id = 1L;
        when(service.getById(id)).thenReturn(leaveApplication);
        ResponseEntity<LeaveApplication> response = controller.getById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leaveApplication, response.getBody());
    }

    @Test
    void getApprovedList() {
        when(service.getAllApprovedLeaveRequests()).thenReturn(approvedDTOList);
        ResponseEntity<List<UserLeaveApplicationDTO>> response = controller.getApprovedList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(approvedDTOList.size(), response.getBody().size());
    }

    @Test
    void getApprovedListByName() {
        String name = "imran";
        when(service.getApprovedListByName(name)).thenReturn(approvedDTOList);
        ResponseEntity<List<UserLeaveApplicationDTO>> response = controller.getApprovedListByName(name);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(approvedDTOList.size(), response.getBody().size());
    }

    @Test
    void approveLeaveRequest() {
        Long id = 1L;
        when(service.updateRequestStatus(id, ApplicationStatus.Approved)).thenReturn(leaveApplication);
        ResponseEntity<RestResponse> response = controller.approveLeaveRequest(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Leave application has been approved", response.getBody().getMessage());
    }

    @Test
    void rejectLeaveRequest() {
        Long id = 1L;
        when(service.updateRequestStatus(id, ApplicationStatus.Rejected)).thenReturn(leaveApplication);
        ResponseEntity<RestResponse> response = controller.rejectLeaveRequest(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Leave application has been rejected", response.getBody().getMessage());
    }

    @Test
    void cancelLeaveRequest() {
        Long id = 1L;
        when(service.updateRequestStatus(id, ApplicationStatus.Canceled)).thenReturn(leaveApplication);
        ResponseEntity<RestResponse> response = controller.cancelLeaveRequest(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Leave application has been canceled", response.getBody().getMessage());
    }

    @Test
    void getAllLeaveDates() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(users);

        Long userId = 1L;
        Long id = 1L;

        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(LocalDate.of(2023, 4,17));
        localDateList.add(LocalDate.of(2023, 4,18));

        when(service.getAllLeaveDates(userId, id)).thenReturn(localDateList);

        ResponseEntity<List<LocalDate>> response = controller.getAllLeaveDates(id, auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(localDateList.size(), response.getBody().size());
    }

    @Test
    void getAnnualLeaveCountStatus() {
        LocalDate fromDate = LocalDate.of(2023, 12,12);
        LocalDate toDate = LocalDate.of(2023, 12,13);
        ZonedDateTime fromTime = ZonedDateTime.of(fromDate.atTime(0,0,0), ZoneId.of("UTC"));
        Long fromDateMil = fromTime.toInstant().toEpochMilli();
        ZonedDateTime toTime = ZonedDateTime.of(toDate.atTime(0,0,0), ZoneId.of("UTC"));
        Long toDateMil = toTime.toInstant().toEpochMilli();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(users);
        Long userId = 1L;
        Long id = 1L;

        when(service.getLeaveCountBalance(fromDateMil, toDateMil, userId, id)).thenReturn(20);

        ResponseEntity<RestResponse> response = controller.getAnnualLeaveCountStatus(fromDateMil, toDateMil, id, auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isStatus());
    }
}
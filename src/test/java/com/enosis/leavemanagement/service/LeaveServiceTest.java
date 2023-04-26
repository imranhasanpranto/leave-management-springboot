package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.LeaveDaysDTO;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.enums.LeaveType;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.interfaces.ProjectDateRange;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock FileService fileService;
    @Mock LeaveRepository repository;
    @Mock UserService userService;
    @Mock UserLeaveCountService leaveCountService;
    @Mock UserLeaveDaysService leaveDaysService;

    @InjectMocks LeaveService service;


    LeaveApplication application;
    Users users;

    @BeforeEach
    void setUp(){
        application = LeaveApplication.builder()
                .id(1l)
                .applicationStatus(ApplicationStatus.Pending)
                .leaveType(LeaveType.Sick)
                .leaveReason("fever")
                .leaveCount(3)
                .emergencyContact("imran")
                .fromDate(LocalDateTime.of(2023, 12, 10, 0, 0, 0))
                .toDate(LocalDateTime.of(2023, 12, 10, 0, 0, 0))
                .build();

        users = Users.builder()
                .id(1l)
                .name("imran")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Admin)
                .build();
    }

    @Test
    void updateLeaveApplication() {
    }

    @Test
    void saveLeaveApplication() {
    }

    @Test
    void getLeaveCount() {
        LeaveService tempService = new LeaveService(repository, fileService, userService, leaveCountService, leaveDaysService);
        LeaveService spyService = spy(tempService);

        LocalDate fromDate = LocalDate.of(2023, 4,13);
        LocalDate toDate = LocalDate.of(2023, 4,20);
        Long userId = 1L;
        Long id = 2L;

        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(LocalDate.of(2023, 4,17));
        localDateList.add(LocalDate.of(2023, 4,18));

        doReturn(localDateList).when(spyService).getAllLeaveDates(userId, id);

        LeaveDaysDTO actual = spyService.getLeaveCount(fromDate, toDate, userId, id);

        assertEquals(4, actual.getLeaveCount());
    }

    @Test
    void updateRequestStatus() {
        Long appId = 1l;
        when(repository.findById(appId)).thenReturn(Optional.of(application));
        LeaveApplication leaveApplication = service.updateRequestStatus(appId, ApplicationStatus.Approved);

        verify(repository).findById(appId);
        assertEquals(ApplicationStatus.Approved, leaveApplication.getApplicationStatus());
    }

    @Test
    void getById() {
    }

    @Test
    void getAllPendingLeaveRequestsAdmin() {
        String email = "x@gmail.com";

        List<UserLeaveApplicationDTO> list = new ArrayList<>();
        list.add(UserLeaveApplicationDTO.builder().userId(1l).applicationStatus(ApplicationStatus.Pending).build());
        list.add(UserLeaveApplicationDTO.builder().userId(2l).applicationStatus(ApplicationStatus.Pending).build());
        list.add(UserLeaveApplicationDTO.builder().userId(3l).applicationStatus(ApplicationStatus.Pending).build());

        when(userService.findByEmail(email)).thenReturn(Optional.of(users));
        when(repository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Pending)).thenReturn(list);

        List<UserLeaveApplicationDTO> listActual = service.getAllPendingLeaveRequests(email);

        assertIterableEquals(list, listActual);
    }

    @Test
    void getAllPendingLeaveRequestsEmployee(){

        String email = "x@gmail.com";

        List<UserLeaveApplicationDTO> list = new ArrayList<>();
        list.add(UserLeaveApplicationDTO.builder().id(1l).userId(1l).applicationStatus(ApplicationStatus.Pending).build());
        list.add(UserLeaveApplicationDTO.builder().id(2l).userId(1l).applicationStatus(ApplicationStatus.Pending).build());
        list.add(UserLeaveApplicationDTO.builder().id(3l).userId(1l).applicationStatus(ApplicationStatus.Pending).build());

        when(userService.findByEmail(email)).thenReturn(Optional.of(users));
        when(repository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Pending)).thenReturn(list);
        List<UserLeaveApplicationDTO> listActual = service.getAllPendingLeaveRequests(email);
        assertIterableEquals(list, listActual);

    }

    @Test
    void getAllApprovedLeaveRequests() {
        List<UserLeaveApplicationDTO> list = new ArrayList<>();
        list.add(UserLeaveApplicationDTO.builder().id(1l).userId(1l).applicationStatus(ApplicationStatus.Approved).build());
        list.add(UserLeaveApplicationDTO.builder().id(2l).userId(2l).applicationStatus(ApplicationStatus.Approved).build());
        list.add(UserLeaveApplicationDTO.builder().id(3l).userId(3l).applicationStatus(ApplicationStatus.Approved).build());

        when(repository.findByApplicationStatusOrderByIdDesc(ApplicationStatus.Approved)).thenReturn(list);
        List<UserLeaveApplicationDTO> listActual = service.getAllApprovedLeaveRequests();
        assertIterableEquals(list, listActual);
    }

    @Test
    void getApprovedListByName() {
        String name = "imran";
        List<UserLeaveApplicationDTO> list = new ArrayList<>();
        list.add(UserLeaveApplicationDTO.builder().id(1l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());
        list.add(UserLeaveApplicationDTO.builder().id(2l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());
        list.add(UserLeaveApplicationDTO.builder().id(3l).userName("imran").applicationStatus(ApplicationStatus.Approved).build());

        when(repository.findApprovedListByName(name, ApplicationStatus.Approved)).thenReturn(list);
        List<UserLeaveApplicationDTO> listActual = service.getApprovedListByName(name);
        assertIterableEquals(list, listActual);
    }

    @Test
    void getAllLeaveDates() {
        Long userId = 1l;
        Long id = 2l;

        List<ProjectDateRange> projectDateRangeList = new ArrayList<>();
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        ProjectDateRange range = factory.createProjection(ProjectDateRange.class);
        range.setFromDate(LocalDateTime.of(2023, 4, 17, 0,0,0));
        range.setToDate(LocalDateTime.now());
        projectDateRangeList.add(range);

        when(repository.findByUserIdAndIdNotAndApplicationStatusIn(userId, id, List.of(ApplicationStatus.Pending, ApplicationStatus.Approved))).thenReturn(projectDateRangeList);
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(LocalDate.of(2023, 4,17));
        localDateList.add(LocalDate.of(2023, 4,18));

        List<LocalDate> actualList = service.getAllLeaveDates(userId, id);

        verify(repository).findByUserIdAndIdNotAndApplicationStatusIn(userId, id, List.of(ApplicationStatus.Pending, ApplicationStatus.Approved));
        assertIterableEquals(localDateList, actualList);
    }

    @Test
    void getLeaveCountBalance() {
        LeaveService tempService = new LeaveService(repository, fileService, userService, leaveCountService, leaveDaysService);
        LeaveService spyService = spy(tempService);

        LocalDate fromDate = LocalDate.of(2023, 12,12);
        LocalDate toDate = LocalDate.of(2023, 12,13);
        Long userId = 1L;
        Long id = 2L;

        LeaveDaysDTO leaveDaysDTO = LeaveDaysDTO.builder()
                .leaveDays(new ArrayList<>())
                .leaveCount(2)
                .build();
        UserLeaveCount count = UserLeaveCount.builder()
                        .id(2L)
                        .userId(1L)
                        .year(2023)
                        .value(20)
                        .build();


        doReturn(leaveDaysDTO).when(spyService).getLeaveCount(fromDate, toDate, userId, id);
        when(leaveCountService.getLeaveBalance(userId)).thenReturn(count);

        ZonedDateTime fromTime = ZonedDateTime.of(fromDate.atTime(0,0,0), ZoneId.of("UTC"));
        Long fromDateMil = fromTime.toInstant().toEpochMilli();
        ZonedDateTime toTime = ZonedDateTime.of(toDate.atTime(0,0,0), ZoneId.of("UTC"));
        Long toDateMil = toTime.toInstant().toEpochMilli();

        int actual = spyService.getLeaveCountBalance(fromDateMil, toDateMil, userId, id);
        assertEquals(18, actual);
    }
}
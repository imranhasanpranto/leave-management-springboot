package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.dto.UserLeaveApplicationDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.exceptions.FileSaveException;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave/application")
@RequiredArgsConstructor
@Slf4j
public class LeaveRestController {
    private final LeaveService leaveService;
    @PostMapping("/add")
    public ResponseEntity<RestResponse> addApplication(
            @ModelAttribute LeaveApplicationDTO leaveApplicationDTO,
            Authentication authentication)throws FileSaveException, IllegalArgumentException {
        Users users = (Users) authentication.getPrincipal();
        log.info("Leave Application DTO:- {}", leaveApplicationDTO);
        String message = leaveService.saveLeaveApplication(leaveApplicationDTO, users.getId());
        RestResponse response = RestResponse.builder().message(message).build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<RestResponse> updateApplication(
            @ModelAttribute LeaveApplicationDTO leaveApplicationDTO,
            Authentication authentication) throws FileSaveException, IllegalArgumentException {
        Users users = (Users) authentication.getPrincipal();
        String message = leaveService.updateLeaveApplication(leaveApplicationDTO, users.getId());
        RestResponse response = RestResponse.builder().message(message).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendingList")
    public ResponseEntity<List<UserLeaveApplicationDTO>> getPendingList(Authentication authentication){
        Users users = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(leaveService.getAllPendingLeaveRequests(users.getEmail()));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<LeaveApplication> getById(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.getById(id));
    }

    @GetMapping("/approvedList")
    public ResponseEntity<List<UserLeaveApplicationDTO>> getApprovedList(){
        return ResponseEntity.ok(leaveService.getAllApprovedLeaveRequests());
    }

    @GetMapping("/approvedListByName/{name}")
    public ResponseEntity<List<UserLeaveApplicationDTO>> getApprovedListByName(@PathVariable String name){
        return ResponseEntity.ok(leaveService.getApprovedListByName(name));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<RestResponse> approveLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Approved);
        RestResponse response = RestResponse.builder().message("Leave application has been approved").build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<RestResponse> rejectLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Rejected);
        RestResponse response = RestResponse.builder().message("Leave application has been rejected").build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<RestResponse> cancelLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Canceled);
        RestResponse response = RestResponse.builder().message("Leave application has been canceled").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllLeaveDates/{id}")
    public ResponseEntity<List<LocalDate>> getAllLeaveDates(@PathVariable Long id, Authentication authentication){
        Users users = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(leaveService.getAllLeaveDates(users.getId(), id));
    }

    @GetMapping("/isAnnualLeaveCountExceeds/{fromDate}/{toDate}/{id}")
    public ResponseEntity<RestResponse> getAnnualLeaveCountStatus(
            @PathVariable Long fromDate,
            @PathVariable Long toDate,
            @PathVariable Long id,
            Authentication authentication
    ){
        Users users = (Users) authentication.getPrincipal();
        int balance = leaveService.getLeaveCountBalance(fromDate, toDate, users.getId(), id);
        RestResponse response = RestResponse.builder().status(balance < 0).build();
        return ResponseEntity.ok(response);
    }
}

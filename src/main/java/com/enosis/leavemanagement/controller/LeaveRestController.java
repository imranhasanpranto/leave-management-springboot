package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.LeaveApplicationDTO;
import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.enums.ApplicationStatus;
import com.enosis.leavemanagement.model.LeaveApplication;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/leave/application")
@RequiredArgsConstructor
public class LeaveRestController {
    private final LeaveService leaveService;
    @PostMapping("/add")
    public ResponseEntity<RestResponse> addApplication(@ModelAttribute LeaveApplicationDTO leaveApplicationDTO){
        System.out.println("LeaveApplicationDTO: "+ leaveApplicationDTO);
        leaveService.saveLeaveApplication(leaveApplicationDTO);
        RestResponse response = RestResponse.builder().message("leave request added successfully").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendingList")
    public ResponseEntity<List<LeaveApplication>> getPendingList(Authentication authentication){
        Users users = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(leaveService.getAllPendingLeaveRequests(users.getEmail()));
    }

    @GetMapping("/approvedList")
    public ResponseEntity<List<LeaveApplication>> getApprovedList(){
        return ResponseEntity.ok(leaveService.getAllApprovedLeaveRequests());
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<RestResponse> approveLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Approved);
        RestResponse response = RestResponse.builder().message("leave application has been approved").build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<RestResponse> rejectLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Rejected);
        RestResponse response = RestResponse.builder().message("leave application has been rejected").build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<RestResponse> cancelLeaveRequest(@PathVariable Long id){
        leaveService.updateRequestStatus(id, ApplicationStatus.Canceled);
        RestResponse response = RestResponse.builder().message("leave application has been canceled").build();
        return ResponseEntity.ok(response);
    }
}

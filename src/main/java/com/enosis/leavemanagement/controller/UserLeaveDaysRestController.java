package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserLeaveDaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-days")
@RequiredArgsConstructor
public class UserLeaveDaysRestController {
    private final UserLeaveDaysService userLeaveDaysService;

    @GetMapping("/getLeaveDays/{id}")
    public ResponseEntity<List<UserLeaveDays>> getUserCurrentLeaveBalance(@PathVariable Long id) {
        List<UserLeaveDays> userLeaveDays = userLeaveDaysService.findByApplicationId(id);
        return ResponseEntity.ok(userLeaveDays);
    }
}

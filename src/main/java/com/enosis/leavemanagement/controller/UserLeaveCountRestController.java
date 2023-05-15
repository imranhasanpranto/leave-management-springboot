package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.model.UserLeaveCount;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserLeaveCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/annual-leave")
public class UserLeaveCountRestController {
    private final UserLeaveCountService userLeaveCountService;
    @GetMapping("/getLeaveBalance")
    public ResponseEntity<UserLeaveCount> getUserCurrentLeaveBalance(Authentication authentication){
        Users users = (Users) authentication.getPrincipal();

        UserLeaveCount userLeaveCount = userLeaveCountService.getLeaveBalance(users.getId());
        return ResponseEntity.ok(userLeaveCount);
    }
}

package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    @GetMapping("/list")
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }
}

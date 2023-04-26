package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    @GetMapping("/list")
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/isUserEmailTaken/{userEmail}")
    public ResponseEntity<RestResponse> isUserEmailTaken(@PathVariable String userEmail){
        Optional<Users> usersOptional = userService.findByEmail(userEmail);
        RestResponse response = null;
        if(usersOptional.isPresent()){
            response = RestResponse.builder().status(true).build();
        }else{
            response = RestResponse.builder().status(false).build();
        }
        return ResponseEntity.ok(response);
    }
}

package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.AuthenticationRequest;
import com.enosis.leavemanagement.dto.AuthenticationResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin//(origins = "http://localhost:4200")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserDTO userDTO) throws AlreadyExistsException {
        return ResponseEntity.ok(authenticationService.register(userDTO));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}

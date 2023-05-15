package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.exceptions.UnAuthorizedAccessException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.GlobalConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class GlobalConfigController {
    private final GlobalConfigService globalConfigService;
    @PutMapping("/update")
    public ResponseEntity<RestResponse> updateConfig(
            @ModelAttribute GlobalConfig globalConfig,
            Authentication authentication)throws NotFoundException, UnAuthorizedAccessException{
        Users users = (Users) authentication.getPrincipal();
        globalConfigService.updateConfig(globalConfig, users.getRole());
        String message = "updated successfully";
        RestResponse response = RestResponse.builder()
                .message(message)
                .status(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<RestResponse> addConfig(
            @Valid @ModelAttribute GlobalConfig globalConfig,
            Authentication authentication
    )throws AlreadyExistsException, UnAuthorizedAccessException{
        Users users = (Users) authentication.getPrincipal();

        globalConfigService.addConfig(globalConfig, users.getRole());
        String message = "saved successfully";
        RestResponse response = RestResponse.builder()
                .message(message)
                .status(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<GlobalConfig> getByName(@PathVariable String name) {
        GlobalConfig globalConfig = globalConfigService.findByName(name);;
        if(globalConfig == null){
            throw new NotFoundException(
                    name+" Not Found."
            );
        }

        return ResponseEntity.ok(globalConfig);
    }
}

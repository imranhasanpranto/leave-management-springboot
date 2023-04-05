package com.enosis.leavemanagement.controller;

import com.enosis.leavemanagement.dto.RestResponse;
import com.enosis.leavemanagement.exceptions.NotFoundException;
import com.enosis.leavemanagement.model.GlobalConfig;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.service.GlobalConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class GlobalConfigController {
    private final GlobalConfigService globalConfigService;
    @PutMapping("/update")
    public ResponseEntity<RestResponse> updateConfig(@ModelAttribute GlobalConfig globalConfig, Authentication authentication){
        Users users = (Users) authentication.getPrincipal();
        String message = globalConfigService.updateConfig(globalConfig, users.getRole());

        RestResponse response = RestResponse.builder()
                .message(message)
                .status(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<RestResponse> addConfig(@ModelAttribute GlobalConfig globalConfig, Authentication authentication){
        Users users = (Users) authentication.getPrincipal();
        String message = globalConfigService.addConfig(globalConfig, users.getRole());

        RestResponse response = RestResponse.builder()
                .message(message)
                .status(true)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<GlobalConfig> getByName(@PathVariable String name) throws NotFoundException {
        GlobalConfig globalConfig = globalConfigService.findByName(name);
        if(globalConfig == null){
            throw new NotFoundException("config not found");
        }
        return ResponseEntity.ok(globalConfig);
    }
}

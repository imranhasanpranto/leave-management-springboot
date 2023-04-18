package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.AuthenticationRequest;
import com.enosis.leavemanagement.dto.AuthenticationResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.UserLeaveDays;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock UserRepository repository;
    @Mock UserService userService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService service;

    @Test
    void register() {
        String email = "x@gmail.com";
        Users users = Users.builder()
                .id(1l)
                .name("imran")
                .email(email)
                .password("12345")
                .build();
        UserDTO userDTO = UserDTO.builder()
                .name("imran")
                .email(email)
                .password("12345")
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder().token("token").build();

        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        when(repository.save(users)).thenReturn(users);

        AuthenticationService authenticationService = new AuthenticationService(
                passwordEncoder,
                repository,
                jwtService,
                authenticationManager,
                userService
                );
        AuthenticationService spyService = spy(authenticationService);
        doReturn(response).when(spyService).getAuthentication(users);
        doReturn(users).when(spyService).convertUserDtoToEntity(userDTO);

        AuthenticationResponse authResponse = spyService.register(userDTO);

        verify(repository).save(users);
        assertEquals("token", authResponse.getToken());
    }

    @Test
    void authenticate() {
        String email = "x@gmail.com";
        Users users = Users.builder()
                .id(1l)
                .name("imran")
                .email(email)
                .password("12345")
                .build();

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(email)
                .password("12345")
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder().token("token").build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(users));

        AuthenticationService authenticationService = new AuthenticationService(
                passwordEncoder,
                repository,
                jwtService,
                authenticationManager,
                userService
        );
        AuthenticationService spyService = spy(authenticationService);
        doReturn(response).when(spyService).getAuthentication(users);
        doReturn(null).when(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        AuthenticationResponse authResponse = spyService.authenticate(request);

        assertEquals("token", authResponse.getToken());
    }

    @Test
    void getAuthentication() {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", Role.Employee.name());
        claims.put("userId", String.valueOf(100l));

        Users users = Users.builder()
                .id(100l)
                .name("imran")
                .email("x@gmail.com")
                .password("12345")
                .role(Role.Employee)
                .build();

        when(jwtService.generateToken(claims, users)).thenReturn("token");
        AuthenticationResponse response = AuthenticationResponse.builder().token("token").build();

        AuthenticationResponse actualResponse = service.getAuthentication(users);

        assertEquals(response, actualResponse);
    }
}
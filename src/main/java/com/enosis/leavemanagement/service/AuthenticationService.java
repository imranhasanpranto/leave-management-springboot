package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.AuthenticationRequest;
import com.enosis.leavemanagement.dto.AuthenticationResponse;
import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.exceptions.AlreadyExistsException;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthenticationResponse register(UserDTO userDTO) throws AlreadyExistsException{
        if(userService.findByEmail(userDTO.getEmail()).isPresent()){
            throw new AlreadyExistsException("This email is already taken!");
        }
        Users user = convertUserDtoToEntity(userDTO);
        user = userRepository.save(user);

        return getAuthentication(user);
    }

    public Users convertUserDtoToEntity(UserDTO userDTO){
        return Users.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(Role.Employee)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Users user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return getAuthentication(user);
    }

    public AuthenticationResponse getAuthentication(Users user){
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", String.valueOf(user.getId()));
        String jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}

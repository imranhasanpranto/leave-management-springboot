package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public UserDTO convertEntityToDto(Users user){
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

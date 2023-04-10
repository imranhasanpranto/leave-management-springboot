package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers(){
        List<UserDTO> userDTOList = userRepository.findAll().stream()
                .map(user -> convertEntityToDto(user))
                .toList();
        return userDTOList;
    }

    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public UserDTO convertEntityToDto(Users user){
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return userDTO;
    }
}

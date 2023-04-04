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

    @Transactional
    public void save(Users users){
        userRepository.save(users);
    }

    public List<UserDTO> getAllUsers(){
        List<UserDTO> userDTOList = userRepository.findAll().stream()
                .map(user -> convertEntityToDto(user))
                .toList();
        return userDTOList;
    }

    public Optional<Users> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Users convertDtoTOEntity(UserDTO userDTO){
        Users users = Users.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
        return users;
    }

    public UserDTO convertEntityToDto(Users user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}

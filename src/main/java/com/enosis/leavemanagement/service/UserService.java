package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void save(UserDTO userDTO){
        //userRepository.save(userDTO);
    }
}

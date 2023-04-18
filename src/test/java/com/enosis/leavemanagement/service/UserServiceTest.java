package com.enosis.leavemanagement.service;

import com.enosis.leavemanagement.dto.UserDTO;
import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.Users;
import com.enosis.leavemanagement.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;


    @Test
    void getAllUsers() {
        List<Users> list = new ArrayList<>();
        List<UserDTO> expectedList = new ArrayList<>();
        UserDTO userDTO = UserDTO.builder()
                .id(1l)
                .role(Role.Employee)
                .email("x@gmail.com")
                .name("x")
                .build();

        Users users = Users.builder()
                .id(1l)
                .role(Role.Employee)
                .email("x@gmail.com")
                .name("x")
                .password("xyz")
                .build();

        expectedList.add(userDTO);
        list.add(users);

        when(userRepository.findAll()).thenReturn(list);
        List<UserDTO> actualList = userService.getAllUsers();

        assertEquals(expectedList.size(), actualList.size());
        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void findByEmail() {
        String email = "imran@gmail.com";
        Users users = Users.builder()
                .id(1l)
                .role(Role.Employee)
                .email(email)
                .name("x")
                .password("xyz")
                .build();

        userService.findByEmail(email);
        verify(userRepository).findByEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(users));

        Optional<Users> usersOptional= userService.findByEmail(email);
        assertTrue(usersOptional.isPresent());
        assertEquals(users, usersOptional.get());
    }
}
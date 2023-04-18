package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.enums.Role;
import com.enosis.leavemanagement.model.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        String email = "test@gmail.com";
        Users users = Users.builder()
                .email(email)
                .name("Md. Imran Hasan")
                .role(Role.Employee)
                .password("12345")
                .build();

        userRepository.save(users);
    }

    @Test
    void findByEmailExists() {
        String email = "test@gmail.com";
        Optional<Users> usersOptional= userRepository.findByEmail(email);
        boolean result = usersOptional.isPresent();
        assertTrue(result);
    }

    @Test
    void findByEmailNotExists() {
        String email = "testNotExist@gmail.com";
        Optional<Users> usersOptional= userRepository.findByEmail(email);
        boolean result = usersOptional.isPresent();
        assertFalse(result, "Email does not exist.");
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
}
package com.enosis.leavemanagement.model;

import com.enosis.leavemanagement.enums.UserType;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Enumerated
    @Column(name = "user_type", nullable = false)
    private UserType userType;
}

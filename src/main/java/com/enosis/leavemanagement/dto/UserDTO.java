package com.enosis.leavemanagement.dto;

import com.enosis.leavemanagement.enums.Role;
import lombok.*;


@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
}

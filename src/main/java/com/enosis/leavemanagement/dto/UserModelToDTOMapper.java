package com.enosis.leavemanagement.dto;

import com.enosis.leavemanagement.model.Users;

public class UserModelToDTOMapper {
    public UserDTO mapModelToDto(Users user){
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

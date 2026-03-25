package com.skillswap.skillswap.dtos.response.security;


import com.skillswap.skillswap.helper.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data

@AllArgsConstructor
public class UserResponse {
    String username;
    private Long id;
    private String email;
    Role role;
    String avatarUrl;
}

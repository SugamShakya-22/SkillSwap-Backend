package com.skillswap.skillswap.service.security;

import com.skillswap.skillswap.dtos.response.security.UserResponse;
import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void updateUserById(Long id, UserResponse userResponse);
    void deleteUserById(Long id);
}

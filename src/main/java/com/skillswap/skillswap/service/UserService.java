package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.response.UserListingResponse;
import com.skillswap.skillswap.model.User;

import java.util.List;

public interface UserService {

    List<UserListingResponse> getAllUsers();

    User getUserByEmail(String email);
}

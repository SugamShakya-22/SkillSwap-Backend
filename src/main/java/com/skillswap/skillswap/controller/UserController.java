package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs to browse users and their skills")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with their skills")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse(
                "Fetched all users successfully",
                true,
                userService.getAllUsers()
        ));
    }
}

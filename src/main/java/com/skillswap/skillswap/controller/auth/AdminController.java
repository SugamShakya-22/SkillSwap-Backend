package com.skillswap.skillswap.controller.auth;

import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.security.UserResponse;
import com.skillswap.skillswap.service.security.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Tag(name = "Admin-User crud", description = "crud operations for users ")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    @Operation(summary = "Get all the users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse("Successfully fetched users.", true, adminService.getAllUsers()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/{id}")
    @Operation(summary = "Get users by their id")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse("Successfully fetched user.", true, user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    @Operation(summary = "Update user details by their id")
    public ResponseEntity<ApiResponse> updateUserById(@PathVariable Long id, @RequestBody UserResponse userResponse) {
        adminService.updateUserById(id, userResponse);
        return ResponseEntity.ok(new ApiResponse("User updated successfully.", true));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok(new ApiResponse("User deleted successfully.", true));
    }
}

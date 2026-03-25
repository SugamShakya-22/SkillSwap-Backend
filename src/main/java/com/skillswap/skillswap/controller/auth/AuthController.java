package com.skillswap.skillswap.controller.auth;



import com.skillswap.skillswap.dtos.request.security.*;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.exception.ValidationException;
import com.skillswap.skillswap.service.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


//@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse("User registered successfully.", true));
    }


    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        Map<String, String> tokens = authService.login(request);
        return ResponseEntity.ok(new ApiResponse("Login successful", true, tokens));
    }

    @PostMapping("/request-reset")
    @Operation(summary = "Request password reset code")
    public ResponseEntity<ApiResponse> requestReset(@RequestBody LoginRequest loginRequest) {
        authService.sendResetCode(loginRequest.email());
        return ResponseEntity.ok().body(new ApiResponse("Password reset code sent to your email.", true));
    }

    @PostMapping("/verify-reset")
    @Operation(summary = "Verify password reset code and reset password")
    public ResponseEntity<ApiResponse> verifyReset(@RequestBody PasswordResetRequest request) {
        authService.verifyAndResetPassword(request);
        return ResponseEntity.ok().body(new ApiResponse("Password reset successfully.", true));
    }


    @PostMapping("/refresh")
    @Operation(summary = "Generates refresh token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest token) {
        try {
            Map<String, String> refreshed = authService.refreshToken(token);
            return ResponseEntity.ok(new ApiResponse("Successfully created RefreshToken", true, refreshed));
        } catch (ResourceNotFoundException | ValidationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An unexpected error occurred: " + ex.getMessage(), false));
        }
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // JWT subject (email)
        authService.changePassword(email, request);
        return ResponseEntity.ok(
                new ApiResponse("Password changed successfully.", true)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(Authentication authentication) {
        String email = authentication.getName(); // get the current user
        authService.logout(email); // mark as offline
        return ResponseEntity.ok(new ApiResponse("Logged out successfully", true, null));
    }

}
package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.dtos.request.security.*;
import com.skillswap.skillswap.exception.DataNotFoundException;
import com.skillswap.skillswap.exception.DuplicateResourceException;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.exception.ValidationException;
import com.skillswap.skillswap.helper.Role;
import com.skillswap.skillswap.model.Profile;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.ProfileRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.security.AuthService;
import com.skillswap.skillswap.service.security.JwtService;
import com.skillswap.skillswap.service.security.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final ProfileRepository profileRepository;

    // Registers a new user, saves it to the database, and sends a welcome email
    @Override
    public void register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("User already exists with username " + request.email());
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Hash the password
                .passwordLastUpdated(LocalDateTime.now()) // Track when password was set
                .role(Role.USER)
                .build();
        userRepository.save(user);
        // Create profile immediately
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFullName(user.getUsername()); // default full name
        profile.setBio("");                     // empty bio
        profile.setAvatarUrl(null);             // no avatar yet
        profileRepository.save(profile);
        mailService.sendWelcomeMail(user); // Send welcome email
    }


    // Authenticates a user, checks password, validates expiration, and returns access & refresh tokens
    public Map<String, String> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.email()));

        System.out.println("🔍 Login - User ID: " + user.getId() + ", Email: " + user.getEmail());

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResourceNotFoundException("User", "credentials", "Invalid username or password");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime passwordLastUpdated = user.getPasswordLastUpdated();

        if (passwordLastUpdated.isBefore(now.minusYears(1))) {
            mailService.sendPasswordReset(user); // Send reset email if password expired
            throw new DataNotFoundException("Your password has expired. A reset link has been sent to your email.");
        }

        if (passwordLastUpdated.isBefore(now.minusMonths(11))) {
            mailService.sendPasswordAboutToExpire(user); // Warn if password is about to expire
        }

        // Set active status
        user.setIsOnline(true);
        user.setLastSeen(now);
        userRepository.save(user);

        String token = jwtService.generateAccessToken(user); // Generate JWT access token
        String refreshToken = jwtService.generateRefreshToken(user); // Generate refresh token

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken);
        response.put("username",user.getUsername());
        response.put("role",user.getRole().name());
        return response;
    }

    // Sends a password reset code to the user's email
    @Override
    public void sendResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        mailService.sendPasswordReset(user);
    }

    // Verifies a reset code and updates the user's password if valid
    @Override
    public void verifyAndResetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        boolean valid = mailService.verify(request.code(), user); // Verify code

        if (!valid) {
            throw new ValidationException("Invalid or expired code.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword())); // Set new hashed password
        userRepository.save(user);
    }

    // Generates a new access token from a valid refresh token
    @Override
    public Map<String, String> refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtService.isAccessToken(refreshToken)) {
            throw new ValidationException("Invalid refresh token: received access token instead.");
        }

        String email = jwtService.extractAllClaims(refreshToken).getSubject();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String newAccessToken = jwtService.generateAccessToken(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        return tokens;
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        // New password match check
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new ValidationException("New password and confirmation do not match");
        }

        // Prevent reusing same password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ValidationException("New password cannot be same as old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordLastUpdated(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setIsOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        });
    }

}

package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.FileUploadRequest;
import com.skillswap.skillswap.dtos.request.ProfileCreateRequest;
import com.skillswap.skillswap.dtos.request.ProfileUpdateRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.ProfileResponse;
import com.skillswap.skillswap.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "APIs to manage user profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Get logged-in user's profile")
    public ResponseEntity<ApiResponse> getMyProfile() {
        ProfileResponse profile = profileService.getMyProfile();
        return ResponseEntity.ok(
                new ApiResponse(
                        "Profile fetched successfully",
                        true,
                        profile
                )
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get profile by user id (for chat, browse, etc.)")
    public ResponseEntity<ApiResponse> getProfileByUserId(@PathVariable Long userId) {
        ProfileResponse profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(
                new ApiResponse(
                        "Profile fetched successfully",
                        true,
                        profile
                )
        );
    }

    @PostMapping
    @Operation(summary = "Create profile for logged-in user")
    public ResponseEntity<ApiResponse> createProfile(
            @Valid @RequestBody ProfileCreateRequest request
    ) {
        ProfileResponse profile = profileService.createProfile(request);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Profile created successfully",
                        true,
                        profile
                )
        );
    }

    @PutMapping("/me")
    @Operation(summary = "Update logged-in user's profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse profile = profileService.updateProfile(request);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Profile updated successfully",
                        true,
                        profile
                )
        );
    }

    @PostMapping("/upload-avatar")
    @Operation(
            summary = "Upload profile picture",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "multipart/form-data",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(
                                    implementation = FileUploadRequest.class
                            )
                    )
            )
    )
    public ResponseEntity<ApiResponse> uploadAvatar(
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        log.info("📸 Avatar upload request from: {}", authentication.getName());
        log.info("   File name: {}", file.getOriginalFilename());
        log.info("   File size: {} bytes", file.getSize());
        log.info("   Content type: {}", file.getContentType());

        String avatarUrl = profileService.uploadAvatar(file);

        log.info("✅ Avatar uploaded successfully: {}", avatarUrl);

        return ResponseEntity.ok(
                new ApiResponse("Avatar uploaded successfully", true, avatarUrl)
        );
    }
}
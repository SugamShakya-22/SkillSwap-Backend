package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.request.ProfileCreateRequest;
import com.skillswap.skillswap.dtos.request.ProfileUpdateRequest;
import com.skillswap.skillswap.dtos.response.ProfileResponse;
import com.skillswap.skillswap.exception.ConflictException;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.exception.ValidationException;
import com.skillswap.skillswap.model.Profile;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.ProfileRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    // Folder to store uploaded avatars
    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public ProfileResponse getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", email));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId()));

        return mapToResponse(profile, user);
    }

    @Override
    public ProfileResponse getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        User user = profile.getUser();

        return mapToResponse(profile, user);
    }


    @Override
    public ProfileResponse createProfile(ProfileCreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", email));

        if (profileRepository.findByUser(user).isPresent()) {
            throw new ConflictException("Profile already exists for this user");
        }

        Profile profile = Profile.builder()
                .fullName(request.fullName())
                .bio(request.bio())
                .avatarUrl(request.avatarUrl())
                .user(user)
                .build();

        Profile savedProfile = profileRepository.save(profile);

        return mapToResponse(savedProfile, user);
    }

    @Override
    public ProfileResponse updateProfile(ProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId()));

        if (request.fullName() != null) profile.setFullName(request.fullName());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.avatarUrl() != null) profile.setAvatarUrl(request.avatarUrl());

        Profile savedProfile = profileRepository.save(profile);

        return mapToResponse(savedProfile, user);
    }

    /**
     * Upload avatar file and return the URL
     */
    @Override
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // ✅ Return full URL including backend IP
            String backendUrl = "http://192.168.1.75:8080"; // replace with your laptop IP
            return backendUrl + "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }



    // Helper to map Profile + User -> ProfileResponse
    private ProfileResponse mapToResponse(Profile profile, User user) {
        return new ProfileResponse(
                profile.getFullName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                user.getUsername(),
                user.getEmail()
        );
    }
}

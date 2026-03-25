package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.request.ProfileCreateRequest;
import com.skillswap.skillswap.dtos.request.ProfileUpdateRequest;
import com.skillswap.skillswap.dtos.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse getMyProfile();
    ProfileResponse getProfileByUserId(Long userId);
    ProfileResponse createProfile(ProfileCreateRequest request);
    ProfileResponse updateProfile(ProfileUpdateRequest request);
    String uploadAvatar(MultipartFile file);
}

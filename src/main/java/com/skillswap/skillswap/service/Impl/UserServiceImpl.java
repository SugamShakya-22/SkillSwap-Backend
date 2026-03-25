package com.skillswap.skillswap.service.Impl;

import com.skillswap.skillswap.dtos.response.UserListingResponse;
import com.skillswap.skillswap.dtos.response.UserSkillResponse;
import com.skillswap.skillswap.model.Profile;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.model.UserSkill;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Fetches all users from the database and maps them to a list of UserListingResponse DTOs
    @Override
    public List<UserListingResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserListingResponse)
                .collect(Collectors.toList());
    }

    // Fetches a single user by email or throws an exception if not found
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Converts a User entity to UserListingResponse, including profile info and skills
    private UserListingResponse mapToUserListingResponse(User user) {
        Profile profile = user.getProfile();

        // Maps UserSkill entities to UserSkillResponse DTOs
        List<UserSkillResponse> skills = user.getUserSkills() != null
                ? user.getUserSkills().stream()
                .map(this::mapToUserSkillResponse)
                .collect(Collectors.toList())
                : List.of();

        return new UserListingResponse(
                user.getId(),
                profile != null ? profile.getFullName() : user.getUsername(),
                user.getEmail(),
                profile != null ? profile.getAvatarUrl() : null,
                skills
        );
    }

    // Converts a UserSkill entity to UserSkillResponse DTO
    private UserSkillResponse mapToUserSkillResponse(UserSkill userSkill) {
        return new UserSkillResponse(
                userSkill.getUserSkillId(),
                userSkill.getSkill().getSkillId(),
                userSkill.getSkill().getSkillName(),
                userSkill.getLevel(),
                userSkill.getType()
        );
    }
}

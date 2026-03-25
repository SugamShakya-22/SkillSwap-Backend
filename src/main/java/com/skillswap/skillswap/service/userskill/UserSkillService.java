package com.skillswap.skillswap.service.userskill;

import com.skillswap.skillswap.dtos.request.AddUserSkillRequest;
import com.skillswap.skillswap.dtos.request.RemoveUserSkillRequest;
import com.skillswap.skillswap.dtos.request.UpdateSkillLevelRequest;
import com.skillswap.skillswap.dtos.response.UserSkillResponse;
import com.skillswap.skillswap.helper.SkillType;

import java.util.List;

public interface UserSkillService {

    // Add a skill for a user
    UserSkillResponse addSkill(AddUserSkillRequest request, Long userId);

    // Remove a skill from a user
    void removeSkill(RemoveUserSkillRequest request, Long userId);

    // Update a user's skill level
    UserSkillResponse updateSkillLevel(UpdateSkillLevelRequest request, Long userId);

    // Get all skills for a user
    List<UserSkillResponse> getAllSkills(Long userId);

    // Get skills by type (OFFER or REQUEST)
    List<UserSkillResponse> getSkillsByType(Long userId, SkillType type);
}

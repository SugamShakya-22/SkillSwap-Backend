package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.AddUserSkillRequest;
import com.skillswap.skillswap.dtos.request.RemoveUserSkillRequest;
import com.skillswap.skillswap.dtos.request.UpdateSkillLevelRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.UserSkillResponse;
import com.skillswap.skillswap.helper.SkillType;
import com.skillswap.skillswap.service.userskill.impl.UserSkillServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/skills")
@RequiredArgsConstructor
@Tag(name = "User Skill", description = "APIs to manage user skills")
public class UserSkillController {

    private final UserSkillServiceImpl userSkillService;

    @PostMapping("/add/{userId}")
    @Operation(summary = "Add a skill for a user")
    public ResponseEntity<ApiResponse> addSkill(
            @PathVariable Long userId,
            @Valid @RequestBody AddUserSkillRequest request) {

        UserSkillResponse response = userSkillService.addSkill(request, userId);
        return ResponseEntity.ok(new ApiResponse("Skill added successfully", true, response));
    }

    @DeleteMapping("/remove/{userId}")
    @Operation(summary = "Remove a skill from a user")
    public ResponseEntity<ApiResponse> removeSkill(
            @PathVariable Long userId,
            @Valid @RequestBody RemoveUserSkillRequest request) {

        userSkillService.removeSkill(request, userId);
        return ResponseEntity.ok(new ApiResponse("Skill removed successfully", true));
    }

    @PutMapping("/update-level/{userId}")
    @Operation(summary = "Update user's skill level")
    public ResponseEntity<ApiResponse> updateSkillLevel(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateSkillLevelRequest request) {

        UserSkillResponse response = userSkillService.updateSkillLevel(request, userId);
        return ResponseEntity.ok(new ApiResponse("Skill level updated successfully", true, response));
    }

    @GetMapping("/all/{userId}")
    @Operation(summary = "Get all skills for a user")
    public ResponseEntity<ApiResponse> getAllSkills(@PathVariable Long userId) {

        List<UserSkillResponse> skills = userSkillService.getAllSkills(userId);
        return ResponseEntity.ok(new ApiResponse("User skills fetched successfully", true, skills));
    }

    @GetMapping("/type/{userId}/{type}")
    @Operation(summary = "Get skills by type (OFFER or REQUEST)")
    public ResponseEntity<ApiResponse> getSkillsByType(
            @PathVariable Long userId,
            @PathVariable SkillType type) {

        List<UserSkillResponse> skills = userSkillService.getSkillsByType(userId, type);
        return ResponseEntity.ok(new ApiResponse("User skills fetched successfully", true, skills));
    }
}

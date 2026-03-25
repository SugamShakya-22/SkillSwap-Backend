package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.dtos.request.skill.AddSkillRequest;
import com.skillswap.skillswap.dtos.request.skill.UpdateSkillRequest;
import com.skillswap.skillswap.dtos.response.ApiResponse;
import com.skillswap.skillswap.dtos.response.SkillResponse;
import com.skillswap.skillswap.service.skill.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(name = "Skill Management", description = "APIs to manage skills")
public class SkillController {

    private final SkillService skillService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    @Operation(summary = "Add a new skill")
    public ResponseEntity<ApiResponse> addSkill(@Valid @RequestBody AddSkillRequest request) {
        SkillResponse createdSkill = skillService.addSkill(request);
        return ResponseEntity.ok(new ApiResponse("Skill added successfully", true, createdSkill));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update")
    @Operation(summary = "Update an existing skill")
    public ResponseEntity<ApiResponse> updateSkill(@Valid @RequestBody UpdateSkillRequest request) {
        SkillResponse updatedSkill = skillService.updateSkill(request);
        return ResponseEntity.ok(new ApiResponse("Skill updated successfully", true, updatedSkill));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{skillId}")
    @Operation(summary = "Delete a skill by ID")
    public ResponseEntity<ApiResponse> deleteSkill(@PathVariable Long skillId) {
        skillService.removeSkill(skillId);
        return ResponseEntity.ok(new ApiResponse("Skill deleted successfully", true));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{skillId}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<ApiResponse> getSkill(@PathVariable Long skillId) {
        SkillResponse skill = skillService.getSkillById(skillId);
        return ResponseEntity.ok(new ApiResponse("Skill fetched successfully", true, skill));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Get all skills")
    public ResponseEntity<ApiResponse> getAllSkills() {
        List<SkillResponse> skills = skillService.getAllSkills();
        return ResponseEntity.ok(new ApiResponse("All skills fetched successfully", true, skills));
    }
}

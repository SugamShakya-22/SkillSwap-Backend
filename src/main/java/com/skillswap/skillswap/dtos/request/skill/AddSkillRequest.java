package com.skillswap.skillswap.dtos.request.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddSkillRequest(
        @NotBlank(message = "Skill name is required")
        @Size(min = 2, max = 50)
        String skillName,

        @NotBlank(message = "Category is required")
        @Size(min = 3, max = 50)
        String category,

        @Size(max = 255)
        String description
) {}

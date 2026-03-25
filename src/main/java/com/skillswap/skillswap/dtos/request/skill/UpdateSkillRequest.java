package com.skillswap.skillswap.dtos.request.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateSkillRequest(
        @NotNull(message = "Skill ID is required")
        Long skillId,

        @Size(min = 2, max = 50)
        String skillName,

        @Size(min = 3, max = 50)
        String category,

        @Size(max = 255)
        String description
) {}

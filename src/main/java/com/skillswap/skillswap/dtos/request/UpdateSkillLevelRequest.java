package com.skillswap.skillswap.dtos.request;

import com.skillswap.skillswap.helper.SkillLevel;
import jakarta.validation.constraints.NotNull;

public record UpdateSkillLevelRequest(
        @NotNull(message = "UserSkill ID is required")
        Long userSkillId,

        @NotNull(message = "Skill level is required")
        SkillLevel level
) {}

package com.skillswap.skillswap.dtos.request;

import com.skillswap.skillswap.helper.SkillLevel;
import com.skillswap.skillswap.helper.SkillType;

import jakarta.validation.constraints.NotNull;

public record AddUserSkillRequest(
        @NotNull(message = "Skill ID is required")
        Long skillId,

        @NotNull(message = "Skill level is required")
        SkillLevel level,

        @NotNull(message = "Skill type is required")
        SkillType type
) {}

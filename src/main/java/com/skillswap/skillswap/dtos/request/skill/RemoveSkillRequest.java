package com.skillswap.skillswap.dtos.request.skill;

import jakarta.validation.constraints.NotNull;

public record RemoveSkillRequest(
        @NotNull(message = "Skill ID is required")
        Long skillId
) {}

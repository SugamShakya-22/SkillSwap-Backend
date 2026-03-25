package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotNull;

// Request DTO for removing a skill
public record RemoveUserSkillRequest(
        @NotNull Long skillId,
        @NotNull String type
) {}
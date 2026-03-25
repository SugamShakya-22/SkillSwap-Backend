package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMatchRequestRequest(
        @NotNull(message = "fromUserSkillId is required") Long fromUserSkillId,
        @NotNull(message = "toUserId is required") Long toUserId,
        @NotNull(message = "toUserSkillId is required") Long toUserSkillId
) {}

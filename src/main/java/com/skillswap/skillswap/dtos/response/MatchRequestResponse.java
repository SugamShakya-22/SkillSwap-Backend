package com.skillswap.skillswap.dtos.response;

import com.skillswap.skillswap.helper.MatchStatus;

public record MatchRequestResponse(
        Long id,
        Long fromUserId,
        String fromUserName,
        Long toUserId,
        String toUserName,
        Long fromUserSkillId,
        String fromUserSkillName,
        Long toUserSkillId,
        String toUserSkillName,
        MatchStatus status,
        Long completionRequestedById
) {}

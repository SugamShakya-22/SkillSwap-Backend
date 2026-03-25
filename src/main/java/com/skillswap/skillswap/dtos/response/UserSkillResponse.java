package com.skillswap.skillswap.dtos.response;

import com.skillswap.skillswap.helper.SkillLevel;
import com.skillswap.skillswap.helper.SkillType;

public record UserSkillResponse(
        Long userSkillId,
        Long skillId,
        String skillName,
        SkillLevel level,
        SkillType type
) {}

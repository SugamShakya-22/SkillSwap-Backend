package com.skillswap.skillswap.service.skill;

import com.skillswap.skillswap.dtos.request.skill.AddSkillRequest;
import com.skillswap.skillswap.dtos.request.skill.UpdateSkillRequest;
import com.skillswap.skillswap.dtos.response.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse addSkill(AddSkillRequest request);

    SkillResponse updateSkill(UpdateSkillRequest request);

    void removeSkill(Long skillId);

    SkillResponse getSkillById(Long skillId);

    List<SkillResponse> getAllSkills();
}

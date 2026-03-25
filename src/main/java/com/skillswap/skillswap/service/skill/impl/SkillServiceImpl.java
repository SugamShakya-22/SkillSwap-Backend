package com.skillswap.skillswap.service.skill.impl;

import com.skillswap.skillswap.dtos.request.skill.AddSkillRequest;
import com.skillswap.skillswap.dtos.request.skill.UpdateSkillRequest;
import com.skillswap.skillswap.dtos.response.SkillResponse;
import com.skillswap.skillswap.exception.ConflictException;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.model.Skill;
import com.skillswap.skillswap.repository.SkillRepository;
import com.skillswap.skillswap.service.skill.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    // Adds a new skill if it doesn't exist, and returns the saved skill as a DTO
    @Override
    public SkillResponse addSkill(AddSkillRequest request) {
        skillRepository.findBySkillName(request.skillName())
                .ifPresent(s -> {
                    throw new ConflictException("Skill already exists with name: " + request.skillName());
                });

        Skill skill = Skill.builder()
                .skillName(request.skillName())
                .category(request.category())
                .description(request.description())
                .build();

        skill = skillRepository.save(skill); // Save skill to DB
        return mapToResponse(skill); // Convert entity to response DTO
    }

    // Updates an existing skill's details if present, otherwise throws exception
    @Override
    public SkillResponse updateSkill(UpdateSkillRequest request) {
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", String.valueOf(request.skillId())));

        if (request.skillName() != null) skill.setSkillName(request.skillName());
        if (request.category() != null) skill.setCategory(request.category());
        if (request.description() != null) skill.setDescription(request.description());

        skill = skillRepository.save(skill); // Save updated skill
        return mapToResponse(skill); // Convert to DTO
    }

    // Removes a skill by ID, throws exception if skill not found
    @Override
    public void removeSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", String.valueOf(skillId)));
        skillRepository.delete(skill); // Delete from DB
    }

    // Retrieves a skill by ID and converts it to a response DTO
    @Override
    public SkillResponse getSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", String.valueOf(skillId)));
        return mapToResponse(skill);
    }

    // Retrieves all skills from DB and converts them to DTOs
    @Override
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to convert Skill entity to SkillResponse DTO
    private SkillResponse mapToResponse(Skill skill) {
        return new SkillResponse(
                skill.getSkillId(),
                skill.getSkillName(),
                skill.getCategory(),
                skill.getDescription()
        );
    }
}

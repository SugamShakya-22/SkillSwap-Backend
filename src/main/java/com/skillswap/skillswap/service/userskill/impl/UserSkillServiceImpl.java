package com.skillswap.skillswap.service.userskill.impl;

import com.skillswap.skillswap.dtos.request.AddUserSkillRequest;
import com.skillswap.skillswap.dtos.request.RemoveUserSkillRequest;
import com.skillswap.skillswap.dtos.request.UpdateSkillLevelRequest;
import com.skillswap.skillswap.dtos.response.UserSkillResponse;
import com.skillswap.skillswap.exception.ConflictException;
import com.skillswap.skillswap.exception.ResourceNotFoundException;
import com.skillswap.skillswap.model.Skill;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.model.UserSkill;
import com.skillswap.skillswap.repository.SkillRepository;
import com.skillswap.skillswap.repository.UserSkillRepository;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.userskill.UserSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    // Adds a skill to a user if not already present and returns the added skill as a DTO
    @Override
    public UserSkillResponse addSkill(AddUserSkillRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", request.skillId()));

        userSkillRepository.findByUserAndSkillAndType(user, skill, request.type())
                .ifPresent(us -> {
                    throw new ConflictException("User already has this skill as " + request.type());
                });

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .level(request.level())
                .type(request.type())
                .createdAt(LocalDateTime.now())
                .build();

        UserSkill saved = userSkillRepository.save(userSkill);

        return mapToResponse(saved);
    }

    // Removes a specific skill of a user by skill type
    @Override
    public void removeSkill(RemoveUserSkillRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", request.skillId()));

        UserSkill userSkill = userSkillRepository.findByUserAndSkillAndType(user, skill,
                        com.skillswap.skillswap.helper.SkillType.valueOf(request.type().toUpperCase()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserSkill", "userId + skillId + type",
                        userId + " | " + request.skillId() + " | " + request.type()
                ));

        userSkillRepository.delete(userSkill);
    }

    // Updates the level of a user's skill if it belongs to the user
    @Override
    public UserSkillResponse updateSkillLevel(UpdateSkillLevelRequest request, Long userId) {
        UserSkill userSkill = userSkillRepository.findById(request.userSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill", "id", request.userSkillId()));

        if (!userSkill.getUser().getId().equals(userId)) {
            throw new ConflictException("UserSkill does not belong to the user");
        }

        userSkill.setLevel(request.level());
        return mapToResponse(userSkillRepository.save(userSkill));
    }

    // Retrieves all skills associated with a user
    @Override
    public List<UserSkillResponse> getAllSkills(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return userSkillRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Retrieves all skills of a user filtered by type (OFFER or REQUEST)
    @Override
    public List<UserSkillResponse> getSkillsByType(Long userId, com.skillswap.skillswap.helper.SkillType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return userSkillRepository.findByUserAndType(user, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Converts UserSkill entity to UserSkillResponse DTO
    private UserSkillResponse mapToResponse(UserSkill userSkill) {
        return new UserSkillResponse(
                userSkill.getUserSkillId(),
                userSkill.getSkill().getSkillId(),
                userSkill.getSkill().getSkillName(),
                userSkill.getLevel(),
                userSkill.getType()
        );
    }
}

package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.helper.SkillType;
import com.skillswap.skillswap.model.Skill;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    // Get all skills of a user
    List<UserSkill> findByUser(User user);

    // Get all OFFER or REQUEST skills of a user
    List<UserSkill> findByUserAndType(User user, SkillType type);

    // Check if user already has a skill (OFFER / REQUEST)
    Optional<UserSkill> findByUserAndSkillAndType(
            User user,
            Skill skill,
            SkillType type
    );

    // Remove a skill from user
    void deleteByUserAndSkillAndType(
            User user,
            Skill skill,
            SkillType type
    );
}

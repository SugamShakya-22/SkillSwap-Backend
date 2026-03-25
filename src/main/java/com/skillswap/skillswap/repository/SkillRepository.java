package com.skillswap.skillswap.repository;

import com.skillswap.skillswap.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    // Fetch skill by skill name
    Optional<Skill> findBySkillName(String skillName); // optional helper
}

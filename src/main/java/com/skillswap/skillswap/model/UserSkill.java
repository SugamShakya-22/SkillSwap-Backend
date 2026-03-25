package com.skillswap.skillswap.model;

import com.skillswap.skillswap.helper.SkillLevel;
import com.skillswap.skillswap.helper.SkillType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "user_skills",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "skill_id", "type"})
        }
)
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSkillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    @NotNull(message = "Skill is required")
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Skill level is required")
    private SkillLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Skill type is required")
    private SkillType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

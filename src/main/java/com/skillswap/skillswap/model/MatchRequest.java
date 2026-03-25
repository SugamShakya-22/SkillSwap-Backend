package com.skillswap.skillswap.model;

import com.skillswap.skillswap.helper.MatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_id", "to_user_id", "from_user_skill_id", "to_user_skill_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User initiating the swap
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    @NotNull(message = "Requesting user is required")
    private User fromUser;

    // User receiving the swap request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    @NotNull(message = "Target user is required")
    private User toUser;

    // Skill offered by fromUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_skill_id", nullable = false)
    @NotNull(message = "Offered skill is required")
    private UserSkill fromUserSkill;

    // Skill requested from toUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_skill_id", nullable = false)
    @NotNull(message = "Requested skill is required")
    private UserSkill toUserSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING; // PENDING, ACCEPTED, DECLINED

    @Column(nullable = false)
    @Builder.Default
    private boolean fromUserCompleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean toUserCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completion_requested_by")
    private User completionRequestedBy;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

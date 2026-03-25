package com.skillswap.skillswap.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who receives this notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Notification type (SWAP_REQUEST, SWAP_ACCEPTED, etc.)
    @Column(nullable = false)
    private String type;

    // Message to display
    @Column(nullable = false, length = 500)
    private String message;

    // Link to related entity (match request ID, message ID, etc.)
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    // Has user read this?
    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    // When created
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // When read (if at all)
    @Column(name = "read_at")
    private LocalDateTime readAt;
}
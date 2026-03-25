package com.skillswap.skillswap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String fullName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone must be valid")
    private String phone;

    private String avatarUrl;

//    @Min(value = 0, message = "Average rating cannot be negative")
//    @Max(value = 5, message = "Average rating cannot exceed 5")
//    private Double avgRating = 0.0;

    @Min(value = 0, message = "Total reviews cannot be negative")
    private Integer totalReviews = 0;

    private String createdAt;
    private String updatedAt;

    // One-to-One relationship with User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

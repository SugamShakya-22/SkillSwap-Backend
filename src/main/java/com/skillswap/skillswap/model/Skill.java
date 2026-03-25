package com.skillswap.skillswap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "skillName")
        }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 50, message = "Skill name must be between 2 and 50 characters")
    @Column(nullable = false, unique = true)
    private String skillName;

    @NotBlank(message = "Category is required")
    @Size(min = 3, max = 50, message = "Category must be between 3 and 50 characters")
    @Column(nullable = false)
    private String category;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}

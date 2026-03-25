package com.skillswap.skillswap.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileCreateRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100)
        String fullName,

        @Size(max = 500)
        String bio,

        String avatarUrl
) {
}

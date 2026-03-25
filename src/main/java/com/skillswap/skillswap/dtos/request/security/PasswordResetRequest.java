package com.skillswap.skillswap.dtos.request.security;


import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(@NotNull String email,@NotNull String code,@NotNull String newPassword) {
}

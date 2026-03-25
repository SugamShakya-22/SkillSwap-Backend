package com.skillswap.skillswap.dtos.request.security;

import jakarta.validation.constraints.NotNull;

public record RegisterRequest(@NotNull String username,@NotNull String email,@NotNull String password) {

}

package com.skillswap.skillswap.dtos.request.security;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record RefreshTokenRequest(@NotNull String refreshToken) {

}

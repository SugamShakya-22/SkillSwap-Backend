package com.skillswap.skillswap.service.security;

import com.skillswap.skillswap.model.User;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String buildToken(User user, String subject, long expirationTime, String tokenType);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean isAccessToken(String token);
    boolean isRefreshToken(String token);
    Claims extractAllClaims(String token);
    String extractUsername(String token);

    // ✅ Add this method for token validation
    boolean isTokenExpired(String token);
}
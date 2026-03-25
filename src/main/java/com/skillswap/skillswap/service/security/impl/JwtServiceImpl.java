package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.service.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    // Secret key used to sign JWTs
    private final String SECRET = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";

    // Converts the secret into a Key object for signing JWTs
    private final Key jwtSigningKey = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Access token valid for 10 hours
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 10;
    // Refresh token valid for 7 days
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    // Builds a JWT with user info, expiration, and type (access/refresh)
    @Override
    public String buildToken(User user, String subject, long expirationTime, String tokenType) {
        return Jwts.builder()
                .setSubject(subject) // username/email
                .claim("tokenType", tokenType) // Marks the token as access or refresh
                .claim("id", user.getId()) // Add user ID
                .claim("role", user.getRole()) // Add user role
                .setIssuedAt(new Date()) // When token was issued
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiration time
                .signWith(jwtSigningKey, SignatureAlgorithm.HS256) // Sign token using HMAC-SHA256
                .compact();
    }

    // Generates a JWT access token for the user
    @Override
    public String generateAccessToken(User user) {
        return buildToken(user, user.getEmail(), ACCESS_TOKEN_EXPIRATION_TIME, "access");
    }

    // Generates a JWT refresh token for the user
    @Override
    public String generateRefreshToken(User user){
        return buildToken(user, user.getEmail(), REFRESH_TOKEN_EXPIRATION_TIME, "refresh");
    }

    // Checks if the token is an access token
    @Override
    public boolean isAccessToken(String token){
        return "access".equals(extractTokenType(token));
    }

    // Checks if the token is a refresh token
    @Override
    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    // Extracts all claims (payload info) from the JWT
    @Override
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extracts the token type (access or refresh) from claims
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    // Extracts the subject (usually email/username) from the token
    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ NEW: Check if token is expired
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // If we can't get expiration, consider it expired
        }
    }
}
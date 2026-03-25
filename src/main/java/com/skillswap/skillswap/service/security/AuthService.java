package com.skillswap.skillswap.service.security;



import com.skillswap.skillswap.dtos.request.security.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    void register(RegisterRequest request);

    Map<String, String> login(LoginRequest loginRequest);

    Map<String, String> refreshToken(RefreshTokenRequest request);

    void sendResetCode(String email);

    void verifyAndResetPassword(PasswordResetRequest request);

    void changePassword(String email, ChangePasswordRequest request);

    void logout(String email);
}


package com.app.auth.auth_service.service;

import com.app.auth.auth_service.dto.AuthResponse;
import com.app.auth.auth_service.dto.RefreshTokenRequest;
import com.app.auth.auth_service.exception.UserNotFoundException;
import com.app.auth.auth_service.model.RefreshToken;
import com.app.auth.auth_service.model.User;
import com.app.auth.auth_service.repository.RefreshTokenRepository;
import com.app.auth.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration}")
    private long expiresIn;

    @Value("${jwt.access-token-expiration}")
    private long accessExp;

    public RefreshToken createRefreshToken(Long userId, String token) {

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(expiresIn))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);

    }


    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh token")
                );

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token Expired!");
        }

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("No user Exists");
        }

        return refreshToken;

    }

    public void revokeToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = validateRefreshToken(request.getRefreshToken());

        User user = userService.getById(token.getUserId());

        String newAccessToken =
                jwtUtil.generateAccessToken(user.getEmail(), accessExp);

        return new AuthResponse(newAccessToken, request.getRefreshToken());
    }

}

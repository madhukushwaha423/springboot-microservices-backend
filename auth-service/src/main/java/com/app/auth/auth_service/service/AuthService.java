package com.app.auth.auth_service.service;

import com.app.auth.auth_service.dto.AuthResponse;
import com.app.auth.auth_service.dto.LoginRequest;
import com.app.auth.auth_service.dto.RegisterRequest;
import com.app.auth.auth_service.exception.UserAlreadyExistsException;
import com.app.auth.auth_service.exception.UserNotFoundException;
import com.app.auth.auth_service.model.User;
import com.app.auth.auth_service.repository.RefreshTokenRepository;
import com.app.auth.auth_service.repository.UserRepository;
import com.app.auth.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessExp;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExp;


    public AuthResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("user already exists : " + registerRequest.getEmail());
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        AuthResponse response = generateToken(user.getEmail());
        refreshTokenService.createRefreshToken(user.getId(), response.getRefreshToken());

        return response;

    }

    public AuthResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email: " + loginRequest.getEmail())
                );

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials!");
        }

        AuthResponse response = generateToken(user.getEmail());
        refreshTokenService.createRefreshToken(user.getId(), response.getRefreshToken());

        return response;
    }

    private AuthResponse generateToken(String email) {

        return new AuthResponse(
                jwtUtil.generateAccessToken(email, accessExp),
                jwtUtil.generateRefreshToken(email, refreshExp)
        );
    }

}

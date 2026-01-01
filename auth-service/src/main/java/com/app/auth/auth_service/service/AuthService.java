package com.app.auth.auth_service.service;

import com.app.auth.auth_service.dto.AuthResponse;
import com.app.auth.auth_service.dto.LoginRequest;
import com.app.auth.auth_service.dto.RegisterRequest;
import com.app.auth.auth_service.exception.UserAlreadyExistsException;
import com.app.auth.auth_service.exception.UserNotFoundException;
import com.app.auth.auth_service.model.User;
import com.app.auth.auth_service.repository.UserRepository;
import com.app.auth.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

        return generateToken(user.getEmail());

    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found!"));

        User user1 = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email: " + loginRequest.getEmail())
                );

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials!");
        }

        return generateToken(user.getEmail());
    }

    private AuthResponse generateToken(String email) {
        return new AuthResponse(
                jwtUtil.generateToken(email, accessExp),
                jwtUtil.generateToken(email, refreshExp)
        );
    }

}

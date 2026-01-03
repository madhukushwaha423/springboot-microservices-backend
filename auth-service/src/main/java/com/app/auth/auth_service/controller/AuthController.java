package com.app.auth.auth_service.controller;

import com.app.auth.auth_service.dto.AuthResponse;
import com.app.auth.auth_service.dto.LoginRequest;
import com.app.auth.auth_service.dto.RefreshTokenRequest;
import com.app.auth.auth_service.dto.RegisterRequest;
import com.app.auth.auth_service.repository.UserRepository;
import com.app.auth.auth_service.security.JwtUtil;
import com.app.auth.auth_service.service.AuthService;
import com.app.auth.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private final JwtUtil jwtUtil;

    @GetMapping("test")
    public String test(){
        return "OK";
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request){
        return refreshTokenService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Long userId) {
        refreshTokenService.revokeToken(userId);
        return ResponseEntity.ok("Logged out successfully");
    }

}

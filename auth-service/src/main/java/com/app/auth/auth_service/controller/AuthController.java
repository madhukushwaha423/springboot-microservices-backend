package com.app.auth.auth_service.controller;

import com.app.auth.auth_service.dto.AuthResponse;
import com.app.auth.auth_service.dto.LoginRequest;
import com.app.auth.auth_service.dto.RegisterRequest;
import com.app.auth.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

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


}

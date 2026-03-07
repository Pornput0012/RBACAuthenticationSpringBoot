package com.pornput.rbactemplate.controllers;

import com.pornput.rbactemplate.model.rbac.request.LoginRequest;
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest;
import com.pornput.rbactemplate.model.rbac.response.AccessTokenResponse;
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Received register request for username: {}", request.getUsername());
        RegisterResponse response = authService.register(request);
        log.info("Register response - userId: {}", response.getUserId());
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Received login request for username: {}", request.getUsername());
        AccessTokenResponse tokenResponse = authService.login(request, response);
        log.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(tokenResponse);
    }


    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue(required = false) String refreshToken) {
        log.info("Received token refresh request");
        AccessTokenResponse tokenResponse = authService.refresh(refreshToken);
        log.info("Token refresh completed successfully");
        return ResponseEntity.ok(tokenResponse);
    }
}

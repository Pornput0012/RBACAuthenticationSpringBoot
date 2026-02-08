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
        log.info("register request");
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue(required = false) String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}

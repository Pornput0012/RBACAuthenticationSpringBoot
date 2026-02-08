package com.pornput.rbactemplate.controllers;

import com.pornput.rbactemplate.model.rbac.request.LoginRequest;
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest;
import com.pornput.rbactemplate.model.rbac.response.LoginResponse;
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}

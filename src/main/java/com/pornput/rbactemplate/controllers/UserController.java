package com.pornput.rbactemplate.controllers;

import com.pornput.rbactemplate.jwt.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal JwtPrincipal principal) {
        log.info("Profile endpoint accessed by user: {}", principal.getUsername());
        return "Hello " + principal.getUsername();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard() {
        log.info("Admin dashboard endpoint accessed");
        return "Admin dashboard";
    }
}

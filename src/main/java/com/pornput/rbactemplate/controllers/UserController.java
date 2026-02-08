package com.pornput.rbactemplate.controllers;

import com.pornput.rbactemplate.jwt.JwtPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal JwtPrincipal principal) {
        return "Hello " + principal.getUsername();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Admin dashboard";
    }
}

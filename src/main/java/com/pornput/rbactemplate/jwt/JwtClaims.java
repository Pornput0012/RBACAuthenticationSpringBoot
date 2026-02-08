package com.pornput.rbactemplate.jwt;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class JwtClaims {

    private final String subject;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final Map<String, Object> claims;

    public JwtClaims(
            String subject,
            Instant issuedAt,
            Instant expiresAt,
            Map<String, Object> claims
    ) {
        this.subject = subject;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.claims = claims;
    }
}
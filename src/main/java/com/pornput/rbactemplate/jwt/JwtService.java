package com.pornput.rbactemplate.jwt;

import java.util.Map;

public interface JwtService {
    String generateToken(
            String subject,
            Map<String, Object> claims
    );

    JwtClaims verify(String token) throws JwtException;
}

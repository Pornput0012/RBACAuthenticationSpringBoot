package com.pornput.rbactemplate.jwt;

import java.util.Map;

public interface JwtService {
    String generateAccessToken(
            String subject,
            Map<String, Object> claims
    );

    String generateRefreshToken(String subject);

    JwtClaims verifyAccessToken(String token) throws JwtException;
    String verifyRefreshToken(String token) throws JwtException;
}

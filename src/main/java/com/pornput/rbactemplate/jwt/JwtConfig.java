package com.pornput.rbactemplate.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
@Getter
public class JwtConfig {
    private final String secret;
    private final Integer expiration;
    private final String issuer;
}
package com.pornput.rbactemplate.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
@Getter
public class JwtConfig {
    private final String secret;
    private final String secretRefresh;
    private final Integer expiration;
    private final Integer expirationRefresh;
    private final String issuer;
}
package com.pornput.rbactemplate.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
@Getter
@RequiredArgsConstructor
public class CorsProperties {
    private final List<String> allowedOrigins;
    private final List<String> allowedMethods;
    private final boolean allowCredentials;
    private final long maxAge;

    public String[] getAllowedOrigins() {
        return allowedOrigins.toArray(String[]::new);
    }

    public String[] getAllowedMethods() {
        return allowedMethods.toArray(String[]::new);
    }
}

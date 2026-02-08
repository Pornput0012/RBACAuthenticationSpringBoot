package com.pornput.rbactemplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleConfig {
    private final RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    private final DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

    @Bean
    public RoleHierarchy roleHierarchy() {
        hierarchy.setHierarchy("""
                    ROLE_ADMIN > ROLE_USER
                """);
        return hierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy
    ) {
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }


}

package com.pornput.rbactemplate.jwt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtPrincipal {
    private final String username;
    private final String role;

}

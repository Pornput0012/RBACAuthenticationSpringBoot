package com.pornput.rbactemplate.model.rbac.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LoginRequest {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;
}

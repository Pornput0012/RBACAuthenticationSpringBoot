package com.pornput.rbactemplate.model.rbac.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Getter
@Builder
@Jacksonized
public class RegisterRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @Size(min = 8, max = 16)
    private final String password;

    @NotBlank
    private final String confirmPassword;

    private final String role;

    @AssertTrue(message = "password and confirmPassword must match")
    public boolean isValidPassword() {
        return Objects.equals(password, confirmPassword);
    }
}

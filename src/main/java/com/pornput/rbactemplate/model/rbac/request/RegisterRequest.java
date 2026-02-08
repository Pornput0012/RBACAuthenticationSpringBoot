package com.pornput.rbactemplate.model.rbac.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class RegisterRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @Size(min = 8, max = 16)
    private final String password;

    private final String confirmPassword;

    @AssertTrue
    public boolean isValidPassword() {
        return password.equals(confirmPassword);
    }
}

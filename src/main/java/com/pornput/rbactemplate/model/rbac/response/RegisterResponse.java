package com.pornput.rbactemplate.model.rbac.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class RegisterResponse {
    private final Long userId;
    private final String username;
}

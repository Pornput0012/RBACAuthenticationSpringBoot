package com.pornput.rbactemplate.model.rbac.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AccessTokenResponse {
    private final String accessToken;
}

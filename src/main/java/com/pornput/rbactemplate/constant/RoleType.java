package com.pornput.rbactemplate.constant;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum RoleType {

    ADMIN,
    USER;

    public static final RoleType DEFAULT = USER;

    public static RoleType fromString(String value) {
        try {
            return RoleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Invalid role type: '" + value + "'. Allowed values: [" + allowed + "]"
            );
        }
    }
}

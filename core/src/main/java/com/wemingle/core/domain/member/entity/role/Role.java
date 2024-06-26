package com.wemingle.core.domain.member.entity.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    UNVERIFIED_USER("ROLE_UNVERIFIED_USER"),
    ADMIN("ROLE_ADMIN"),
    WITHDRAW_USER("ROLE_WITHDRAW_USER");

    private final String roleName;
}

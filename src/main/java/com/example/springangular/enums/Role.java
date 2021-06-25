package com.example.springangular.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.springangular.constants.Authority.*;

@AllArgsConstructor
@Getter
public enum Role {
    // the enum would be referred to as ROLE_USER and pass the string in parentheses
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private final String[] authorities;
}


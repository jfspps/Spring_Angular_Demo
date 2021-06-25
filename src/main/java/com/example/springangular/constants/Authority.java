package com.example.springangular.constants;

public class Authority {
    // user:read is read user entity info
    public static final String[] USER_AUTHORITIES = { "user:read" };
    public static final String[] HR_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create" };
    public static final String[] SUPER_AUTHORITIES = { "user:create", "user:read", "user:update", "user:delete" };
}

package com.example.springangular.constants;

public class SecurityConstant {

    // milliseconds (5 days)
    public static final long EXPIRATION_TIME = 432_000_000;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";

    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String GET_MY_COMPANY = "My Company, Inc";
    public static final String GET_MY_COMPANY_ADMIN = "User management portal";
    public static final String AUTHORITIES = "authorities";

    public static final String FORBIDDEN_MESSAGE = "Log in to access this resource";
    public static final String ACCESS_DENIED_MESSAGE = "Not permitted to access this resource";

    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/resetpassword/**", "/user/image/**"};

    // use for testing purposes only!
    public static final String[] ALLOW_ALL_URLS = {"**"};

}

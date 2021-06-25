package com.example.springangular.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration.";
    public static final String METHOD_IS_NOT_ALLOWED = "The request method is not allowed.";
    public static final String INTERNAL_SERVER_ERROR = "A server error occurred when processing the request.";
    public static final String INCORRECT_CREDENTIALS = "Username or password are incorrect.";
    public static final String ACCOUNT_DISABLED = "Your account has been disabled. Please contact administration.";
    public static final String FILE_PROCESSING_ERROR = "An error occurred while processing file.";
    public static final String NOT_PERMITTED = "You are not permitted to access this resource.";
}

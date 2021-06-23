package com.example.springangular.domain.http;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class HttpResponse {

    private int httpStatusCode;

    // enum of HTTP reason phrases (200 is OK)
    private HttpStatus httpStatus;

    private String reason;

    private String message;
}

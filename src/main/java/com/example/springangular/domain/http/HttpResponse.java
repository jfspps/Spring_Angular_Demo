package com.example.springangular.domain.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

@AllArgsConstructor
@Builder
@Setter
@Getter
public class HttpResponse {

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

    private int httpStatusCode;

    // enum of HTTP reason phrases (200 is OK)
    private HttpStatus httpStatus;

    private String reason;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", locale = "en_GB", timezone = "Europe/London")
    private Date timestamp;
}

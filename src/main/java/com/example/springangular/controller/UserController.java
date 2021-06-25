package com.example.springangular.controller;

import com.example.springangular.exception.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController extends ExceptionHandling {

    @GetMapping("/getMessage")
    public String confirmMsg() {
        return "It works";
    }
}

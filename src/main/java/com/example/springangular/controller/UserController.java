package com.example.springangular.controller;

import com.example.springangular.domain.security.User;
import com.example.springangular.exception.ExceptionHandling;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;
import com.example.springangular.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserController extends ExceptionHandling {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getMessage")
    public String confirmMsg() {
        return "It works";
    }

    // test exception handling
//    @GetMapping("/getEmails")
//    public String getEmails() throws EmailNotFoundException {
//        throw new EmailNotFoundException("Whoops");
//    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)
            throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException {
        User sentUser = userService.register(user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail());

        return new ResponseEntity<>(sentUser, HttpStatus.OK);
    }
}

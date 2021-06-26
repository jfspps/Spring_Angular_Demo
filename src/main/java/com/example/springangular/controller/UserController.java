package com.example.springangular.controller;

import com.example.springangular.domain.security.User;
import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.exception.ExceptionHandling;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;
import com.example.springangular.jwt.JWTTokenProvider;
import com.example.springangular.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.example.springangular.constants.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserController extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
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

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());

        User loggedInUser = userService.findByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loggedInUser);

        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);

        // send the jwt in the header and the user details in the body
        return new ResponseEntity<>(loggedInUser, jwtHeader, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        // throws and exception if auth fails
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}

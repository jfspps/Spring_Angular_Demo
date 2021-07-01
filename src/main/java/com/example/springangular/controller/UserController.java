package com.example.springangular.controller;

import com.example.springangular.domain.http.HttpResponse;
import com.example.springangular.domain.security.User;
import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.exception.ExceptionHandling;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.EmailNotFoundException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;
import com.example.springangular.jwt.JWTTokenProvider;
import com.example.springangular.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.websocket.server.PathParam;

import java.io.IOException;
import java.util.List;

import static com.example.springangular.constants.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/", "/user"})
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
            throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, MessagingException {
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

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, IOException, EmailAlreadyExistException, UsernameAlreadyExistException {

        // expect boolean worded as "true" in any case
        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateNewUser(@RequestParam("currentUsername") String currentUsername,
                                              @RequestParam("firstName") String firstName,
                                              @RequestParam("lastName") String lastName,
                                              @RequestParam("username") String username,
                                              @RequestParam("email") String email,
                                              @RequestParam("role") String role,
                                              @RequestParam("isActive") String isActive,
                                              @RequestParam("isNonLocked") String isNonLocked,
                                              @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, IOException, EmailAlreadyExistException, UsernameAlreadyExistException {

        // expect boolean worded as "true" in any case
        User user = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable("username") String username){
        User user = userService.findByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> findByUsername(){
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(HttpStatus.OK, "New password sent to: " + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") String id){
        userService.deleteUserById(Long.parseLong(id));
        return response(HttpStatus.NO_CONTENT, "User removed from the database");
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
                                           @RequestParam("username") String username,
                                           @RequestParam("profileImage") MultipartFile profileImage)
            throws UserNotFoundException, IOException, EmailAlreadyExistException, UsernameAlreadyExistException {

        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        // first parameter is the httpResponse body
        return new ResponseEntity<>(new HttpResponse(status.value(), status, status.getReasonPhrase(), message), status);
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

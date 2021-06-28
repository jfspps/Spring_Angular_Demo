package com.example.springangular.service;

import com.example.springangular.domain.security.User;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, MessagingException;

    List<User> findAllUsers();

    User findByUsername(String username);

    User findByEmail(String email);
}

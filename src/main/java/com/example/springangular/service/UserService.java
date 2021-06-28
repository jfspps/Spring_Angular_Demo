package com.example.springangular.service;

import com.example.springangular.domain.security.User;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, MessagingException;

    List<User> findAllUsers();

    User findByUsername(String username);

    User findByEmail(String email);

    /**
     * Add a new user as a logged in admin user
      */
    User addNewUser(String firstName, String lastName, String username,
                    String email, String role, boolean isNonLocked, boolean isActive,
                    MultipartFile profileImage);

    /**
     * Update a user as a logged in admin user
     */
    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                    String newEmail, String role, boolean isNonLocked, boolean isActive,
                    MultipartFile profileImage);

    void deleteUserById(long id);

    void resetPassword(String email);

    User updateProfileImage(String username, MultipartFile image);
}

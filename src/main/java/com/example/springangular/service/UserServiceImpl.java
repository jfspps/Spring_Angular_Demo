package com.example.springangular.service;

import com.example.springangular.domain.security.User;
import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.enums.Role;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.UserNotFoundException;
import com.example.springangular.exception.domain.UsernameAlreadyExistException;
import com.example.springangular.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional      // manage propagating operations per transaction
@Qualifier("UserDetailsService")        // force Spring to use this class; see SecurityConfiguration
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String USERNAME_ALREADY_IN_USE = "Username already in use";

    // get this class, UserServiceImpl
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            LOGGER.error("User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());

            user.setLastLoginDate(new Date());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("User last login updated");
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);

        User user = User.builder().userId(generateUserId()).build();

        String password = generatePassword();
        String encodedPassword = encodePassword(password);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTempProfileImageURL());

        userRepository.save(user);
        LOGGER.info("New user password: " + password);

        return user;
    }

    private String getTempProfileImageURL() {
        // http://localhost:8081 fromCurrentContextPath
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        // random password of ten characters
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        // random integer of ten digits
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String email)
            throws UsernameAlreadyExistException, EmailAlreadyExistException, UserNotFoundException {
        User userNyNewUsername = findByUsername(newUsername);
        User userByEmail = findByEmail(email);

        if (StringUtils.isNotEmpty(currentUsername)){
            User currentUser = findByUsername(currentUsername);
            if (currentUser == null){
                throw new UserNotFoundException("User not found with username: " + currentUsername);
            }
            if (userNyNewUsername != null && !currentUser.getUserId().equals(userNyNewUsername.getUserId())){
                throw new UsernameAlreadyExistException(USERNAME_ALREADY_IN_USE);
            }
            if (userByEmail != null && !currentUser.getUserId().equals(userByEmail.getUserId())){
                throw new EmailAlreadyExistException(EMAIL_ALREADY_IN_USE);
            }
            return currentUser;
        } else {
            if (userNyNewUsername != null){
                throw new UsernameAlreadyExistException(USERNAME_ALREADY_IN_USE);
            }
            if (userByEmail != null){
                throw new EmailAlreadyExistException(EMAIL_ALREADY_IN_USE);
            }
            return null;
        }
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}

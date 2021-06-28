package com.example.springangular.service;

import com.example.springangular.domain.security.User;
import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.enums.Role;
import com.example.springangular.exception.domain.EmailAlreadyExistException;
import com.example.springangular.exception.domain.EmailNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.example.springangular.constants.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional      // manage propagating operations per transaction
@Qualifier("UserDetailsService")        // force Spring to use this class; see SecurityConfiguration
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String USERNAME_ALREADY_IN_USE = "Username already in use";
    private static final String NO_USER_FOUND_BY_EMAIL = "No user found with given email: ";

    // get this class, UserServiceImpl
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final LoginAttemptService loginAttemptService;

    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, LoginAttemptService loginAttemptService, EmailService emailService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            LOGGER.error("User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            validateLoginAttempts(user);
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
            throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, MessagingException {
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
        user.setProfileImageUrl(getTempProfileImageURL(username));

        userRepository.save(user);
//        LOGGER.info("New user password: " + password);
        emailService.sendNewPasswordEmail(firstName, password, email);
        return user;
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

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();

        String password = generatePassword();
        String encodedPassword = encodePassword(password);

        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTempProfileImageURL(username));
        userRepository.save(user);

        saveProfileImage(user, profileImage);

        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);

        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);

        saveProfileImage(currentUser, profileImage);

        return currentUser;
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User userFound = userRepository.findUserByEmail(email);
        if (userFound == null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        userFound.setPassword(encodePassword(password));
        userRepository.save(userFound);
        emailService.sendNewPasswordEmail(userFound.getFirstName(), password, userFound.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile image) throws UserNotFoundException, EmailAlreadyExistException, UsernameAlreadyExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, image);
        return user;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            // replace existing comes from Java NIO
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        // http://localhost:8081 fromCurrentContextPath
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private void validateLoginAttempts(User user) {
        if (user.isNotLocked()){
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.releaseUserFromCache(user.getUsername());
        }
    }

    private String getTempProfileImageURL(String username) {
        // http://localhost:8081 fromCurrentContextPath
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toString();
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
}

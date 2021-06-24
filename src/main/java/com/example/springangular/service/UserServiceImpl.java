package com.example.springangular.service;

import com.example.springangular.domain.security.User;
import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional      // manage propagating operations per transaction
@Qualifier("UserDetailsService")        // force Spring to use this class; see SecurityConfiguration
public class UserServiceImpl implements UserService, UserDetailsService {

    // get this class, UserServiceImpl
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

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
}

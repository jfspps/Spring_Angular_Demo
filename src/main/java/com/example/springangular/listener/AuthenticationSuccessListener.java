package com.example.springangular.listener;

import com.example.springangular.domain.security.UserPrincipal;
import com.example.springangular.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        // this should be a UserPrincipal object
        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal){
            UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.releaseUserFromCache(userPrincipal.getUsername());
        }
    }
}

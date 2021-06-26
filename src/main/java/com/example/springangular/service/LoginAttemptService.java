package com.example.springangular.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAXIMUM_LOGIN_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 1;

    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .maximumSize(100)   // limit the number of users in the cache to 100
                .build(new CacheLoader<>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void releaseUserFromCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToCache(String username) throws ExecutionException {
        int attempts = loginAttemptCache.get(username) + ATTEMPT_INCREMENT;
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_LOGIN_ATTEMPTS;
    }
}

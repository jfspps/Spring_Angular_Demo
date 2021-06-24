package com.example.springangular.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springangular.domain.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.example.springangular.constants.SecurityConstant.*;

public class JWTTokenProvider {

    // usually have this as part of a config file at deployment
    // (currently retrieved from application.properties)
    @Value("jwt.secret")
    private String secret;

    public String generateJwtToken(UserPrincipal principal){
        String[] claims = getUserClaims(principal);
        return JWT.create()
                .withIssuer(GET_MY_COMPANY)
                .withAudience(GET_MY_COMPANY_ADMIN)
                .withIssuedAt(new Date())
                .withSubject(principal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes(StandardCharsets.UTF_8)));
    }

    private String[] getUserClaims(UserPrincipal principal) {
        return null;
    }
}

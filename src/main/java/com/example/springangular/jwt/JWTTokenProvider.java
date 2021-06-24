package com.example.springangular.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.springangular.domain.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.springangular.constants.SecurityConstant.*;
import static java.util.Arrays.stream;

public class JWTTokenProvider {

    // for documentation, see https://github.com/auth0/java-jwt

    // usually have this as part of a config file at deployment
    // (currently retrieved from application.properties)
    @Value("${jwt.secret}")
    private String secret;

    // once authenticated, build a JWT
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
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : principal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    // with subsequent access to the service, get authorities based on the JWT
    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getTokenClaims(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getTokenClaims(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}

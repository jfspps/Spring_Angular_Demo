package com.example.springangular.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.springangular.constants.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.example.springangular.constants.SecurityConstant.TOKEN_PREFIX;

@Component
public class JWTAuthorisationFilter extends OncePerRequestFilter {

    private final JWTTokenProvider jwtTokenProvider;

    public JWTAuthorisationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // request is issued from client;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // allow HTTP OPTIONS request from client to pass through (OPTIONS is used to gather server info)
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)){
            response.setStatus(HttpStatus.OK.value());
        } else {
            // parse the Authorisation header
            String authorisationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // check for "Bearer " header
            if (authorisationHeader == null || !authorisationHeader.startsWith(TOKEN_PREFIX)){
                // ignore the authorisation header and stop
                filterChain.doFilter(request, response);
                return;
            }

            // remove "Bearer "
            String token = authorisationHeader.substring(TOKEN_PREFIX.length());

            // get subject (user)
            String username = jwtTokenProvider.getSubject(token);

            // if sessions are not managed then the second criteria can be omitted
            if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null){
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                // inform Spring that the user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // remove any user from the session context
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        }
    }
}

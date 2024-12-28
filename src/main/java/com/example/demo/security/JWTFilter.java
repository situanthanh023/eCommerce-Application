package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.example.demo.model.persistence.User;
import com.example.demo.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authentication;

    public JWTFilter(AuthenticationManager authenticationManager) {
        this.authentication = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            User credentials = new ObjectMapper().readValue(req.getInputStream(), User.class);

            String username = credentials.getUsername();
            String password = credentials.getPassword();
            return authentication.authenticate(new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {

        String token = JWT.create()


                .withSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())


                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))

                .sign(HMAC512(Constants.SECRET.getBytes()));

        res.addHeader(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + token);
    }
}
package com.kristalball.mams.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// This class creates the login token and also reads it back
@Component
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // token is valid for 8 hours
    public String createToken(String username) {
        long eightHoursInMillis = 8 * 60 * 60 * 1000L;
        Date expiryTime = new Date(System.currentTimeMillis() + eightHoursInMillis);

        return Jwts.builder()
                .subject(username)
                .expiration(expiryTime)
                .signWith(secretKey)
                .compact();
    }

    // returns the username stored inside the token (throws exception if token is wrong)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}

package com.esl.academy.api.core.services;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class JwtService {

    @Value("${application.jwt.access.secret}")
    private String accessSecret;

    @Value("${application.jwt.access.expiration}")
    private long accessExpiration;

    public String generateToken(String userId, String email) {
        final var builder = Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .issuedAt(Date.from(Instant.now()))
            .signWith(getSignInKey());
        return builder
            .expiration(Date.from(Instant.now().plus(accessExpiration, ChronoUnit.MINUTES)))
            .compact();
    }

    public String generateToken(String subject, Map<String, String> claims, long expiration, ChronoUnit unit) {
        return Jwts.builder()
            .subject((subject))
            .claims(claims)
            .issuedAt(Date.from(Instant.now()))
            .signWith(getSignInKey())
            .expiration(Date.from(Instant.now().plus(expiration, unit)))
            .compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        final var claims = getParser()
            .parseSignedClaims(token)
            .getPayload();
        final var unexpired = claims.getExpiration().after(Date.from(Instant.now()));
        return unexpired && (Objects.equals(claims.get("username"), user.getUsername()));
    }

    public String getSubject(String token) {
        return getParser()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public <T> T getClaim(String token, String key, Class<T> clazz) {
        return getParser()
            .parseSignedClaims(token)
            .getPayload()
            .get(key, clazz);
    }

    public boolean isTokenValid(String token) {
        try {
            return getParser()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().after(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser getParser() {
        return Jwts.parser().verifyWith(getSignInKey()).build();
    }
}

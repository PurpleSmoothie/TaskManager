package com.tema_kuznetsov.task_manager.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        System.out.println("Extracting username from token: " + token);
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String userEmail) {
        try {
            String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(userEmail) && !isTokenExpired(token);
            System.out.println("Token valid: " + isValid); // Логирование результата
            return isValid;
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder() // Используем новый подход
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
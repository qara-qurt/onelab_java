package org.onelab.gateway_cli_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtToken {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 characters).");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Long getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

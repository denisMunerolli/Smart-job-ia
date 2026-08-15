package com.smartjobai.api.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs:604800000}")
    private long jwtExpirationMs; // 7 dias por padrão

    @Value("${app.jwt.refreshExpirationMs:2592000000}")
    private long refreshExpirationMs; // 30 dias por padrão

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /** Gera access token (7 dias) */
    public String generateToken(Authentication authentication) {
        return buildToken(authentication.getName(), jwtExpirationMs);
    }

    /** Gera refresh token (30 dias) */
    public String generateRefreshToken(Authentication authentication) {
        return buildToken(authentication.getName(), refreshExpirationMs);
    }

    /** Gera novo access token a partir de um refresh token válido */
    public String refreshAccessToken(String refreshToken) {
        String email = getUsernameFromToken(refreshToken);
        return buildToken(email, jwtExpirationMs);
    }

    private String buildToken(String subject, long expirationMs) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
        }
        return false;
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}

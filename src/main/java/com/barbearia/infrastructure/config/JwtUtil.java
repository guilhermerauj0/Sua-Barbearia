package com.barbearia.infrastructure.config;

import com.barbearia.application.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementação do serviço JWT usando a biblioteca JJWT.
 * 
 * Responsabilidades:
 * - Gerar tokens JWT com claims personalizados
 * - Validar tokens JWT
 * - Extrair informações dos tokens
 * 
 * Claims incluídos no token:
 * - sub: subject (email do usuário)
 * - userId: ID do usuário
 * - role: papel do usuário (CLIENTE, BARBEIRO, ADMIN)
 * - iat: issued at (data de emissão)
 * - exp: expiration (data de expiração)
 */
@Component
public class JwtUtil implements JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration:3600000}") // Padrão: 1 hora (3600000 ms)
    private Long expiration;
    
    /**
     * Gera a chave secreta para assinar o token.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    @Override
    public String generateToken(Map<String, Object> claims, String subject) {
        return generateToken(claims, subject, expiration);
    }

    @Override
    public String generateToken(Map<String, Object> claims, String subject, Long expirationMillis) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())
                .compact();
    }
    
    @Override
    public String generateToken(String subject) {
        return generateToken(new HashMap<>(), subject);
    }
    
    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    @Override
    public Object extractClaim(String token, String claimName) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claimName);
    }
    
    /**
     * Extrai um claim específico usando uma função.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrai todos os claims do token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    @Override
    public boolean isTokenValid(String token, String subject) {
        final String extractedSubject = extractSubject(token);
        return (extractedSubject.equals(subject) && !isTokenExpired(token));
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extrai a data de expiração do token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    @Override
    public Long getExpirationTime() {
        return expiration;
    }
}

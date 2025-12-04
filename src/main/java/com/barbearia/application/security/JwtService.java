package com.barbearia.application.security;

import java.util.Map;

/**
 * Interface para serviços de JWT (JSON Web Token).
 * Define as operações necessárias para gerar e validar tokens.
 * 
 * Princípio da Inversão de Dependência (SOLID):
 * - A camada de aplicação depende desta interface, não da implementação concreta.
 * - Permite trocar a implementação de JWT no futuro sem afetar o resto do código.
 */
public interface JwtService {
    
    /**
     * Gera um token JWT com claims personalizados.
     * 
     * @param claims Informações extras a serem incluídas no token (userId, role, etc.)
     * @param subject Identificador principal (geralmente o email do usuário)
     * @return Token JWT gerado
     */
    String generateToken(Map<String, Object> claims, String subject);
    
    /**
     * Gera um token JWT com claims personalizados e tempo de expiração específico.
     * 
     * @param claims Informações extras a serem incluídas no token
     * @param subject Identificador principal
     * @param expirationMillis Tempo de expiração em milissegundos
     * @return Token JWT gerado
     */
    String generateToken(Map<String, Object> claims, String subject, Long expirationMillis);

    /**
     * Gera um token JWT apenas com o subject (sem claims extras).
     * 
     * @param subject Identificador principal (geralmente o email do usuário)
     * @return Token JWT gerado
     */
    String generateToken(String subject);
    
    /**
     * Extrai o subject (email) do token JWT.
     * 
     * @param token Token JWT
     * @return Subject extraído do token
     */
    String extractSubject(String token);
    
    /**
     * Extrai um claim específico do token JWT.
     * 
     * @param token Token JWT
     * @param claimName Nome do claim a ser extraído
     * @return Valor do claim
     */
    Object extractClaim(String token, String claimName);
    
    /**
     * Valida se o token JWT é válido e não expirou.
     * 
     * @param token Token JWT
     * @param subject Subject esperado (email do usuário)
     * @return true se o token é válido, false caso contrário
     */
    boolean isTokenValid(String token, String subject);
    
    /**
     * Verifica se o token JWT expirou.
     * 
     * @param token Token JWT
     * @return true se o token expirou, false caso contrário
     */
    boolean isTokenExpired(String token);
    
    /**
     * Obtém o tempo de expiração configurado (em milissegundos).
     * 
     * @return Tempo de expiração em milissegundos
     */
    Long getExpirationTime();
}

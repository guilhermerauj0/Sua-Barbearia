package com.barbearia.application.dto;

/**
 * DTO para resposta de login bem-sucedido.
 * Contém o token JWT e informações do usuário autenticado.
 */
public record LoginResponseDto(
    
    String token,
    
    String tipo,
    
    Long userId,
    
    String nome,
    
    String email,
    
    String role,
    
    Long expiresIn
) {
    /**
     * Construtor padrão com tipo "Bearer"
     */
    public LoginResponseDto(String token, Long userId, String nome, String email, String role, Long expiresIn) {
        this(token, "Bearer", userId, nome, email, role, expiresIn);
    }
}

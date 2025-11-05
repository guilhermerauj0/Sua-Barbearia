package com.barbearia.domain.exceptions;

/**
 * Exceção lançada quando um usuário tenta acessar um recurso sem permissão.
 * 
 * Deve ser tratada no controller para retornar HTTP 403 (Forbidden).
 * 
 * @author Sua Barbearia Team
 */
public class AcessoNegadoException extends RuntimeException {
    
    public AcessoNegadoException(String message) {
        super(message);
    }
    
    public AcessoNegadoException(String message, Throwable cause) {
        super(message, cause);
    }
}

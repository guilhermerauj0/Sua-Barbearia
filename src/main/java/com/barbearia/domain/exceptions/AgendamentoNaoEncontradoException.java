package com.barbearia.domain.exceptions;

/**
 * Exceção lançada quando um agendamento não é encontrado.
 * 
 * Deve ser tratada no controller para retornar HTTP 404 (Not Found).
 * 
 * @author Sua Barbearia Team
 */
public class AgendamentoNaoEncontradoException extends RuntimeException {
    
    public AgendamentoNaoEncontradoException(String message) {
        super(message);
    }
    
    public AgendamentoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.barbearia.domain.exceptions;

/**
 * Exceção lançada quando um cliente não é encontrado no sistema.
 * 
 * <p>Casos de uso:</p>
 * <ul>
 *   <li>Cliente não existe no banco de dados</li>
 *   <li>Cliente não pertence à barbearia consultada</li>
 *   <li>Cliente foi anonimizado (LGPD)</li>
 * </ul>
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
public class ClienteNaoEncontradoException extends RuntimeException {
    
    /**
     * Construtor com mensagem personalizada.
     * 
     * @param mensagem Mensagem descritiva do erro
     */
    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    /**
     * Construtor com mensagem e causa.
     * 
     * @param mensagem Mensagem descritiva do erro
     * @param causa Exceção que causou este erro
     */
    public ClienteNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

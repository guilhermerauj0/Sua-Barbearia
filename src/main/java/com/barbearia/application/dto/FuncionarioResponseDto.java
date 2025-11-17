package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de funcionário.
 * 
 * @author Sua Barbearia Team
 */
public record FuncionarioResponseDto(
    Long id,
    Long barbeariaId,
    String nome,
    String email,
    String telefone,
    String profissao,
    boolean ativo,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {
}

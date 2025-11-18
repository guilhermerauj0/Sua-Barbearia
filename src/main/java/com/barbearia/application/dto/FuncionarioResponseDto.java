package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoPerfil;
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
    TipoPerfil perfilType,
    String profissao,
    String especialidades,
    boolean ativo,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {
}

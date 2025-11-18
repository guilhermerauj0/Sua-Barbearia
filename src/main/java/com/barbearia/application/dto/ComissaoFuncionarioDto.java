package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoPerfil;
import java.math.BigDecimal;

/**
 * DTO para representar comissões de um funcionário em um período.
 * 
 * @author Sua Barbearia Team
 */
public record ComissaoFuncionarioDto(
    Long funcionarioId,
    String funcionarioNome,
    String funcionarioEmail,
    TipoPerfil perfilType,
    String profissao,
    Double taxaComissao,
    Integer totalServicos,
    BigDecimal valorTotalServicos,
    BigDecimal totalComissoes
) {
}

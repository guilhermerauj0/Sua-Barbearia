package com.barbearia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de cadastro/atualização de funcionário.
 * 
 * @author Sua Barbearia Team
 */
public record FuncionarioRequestDto(
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 10, max = 20, message = "Telefone deve ter entre 10 e 20 caracteres")
    String telefone,
    
    @NotBlank(message = "Profissão é obrigatória")
    String profissao
) {
}

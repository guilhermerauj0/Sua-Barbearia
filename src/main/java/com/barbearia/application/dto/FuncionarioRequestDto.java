package com.barbearia.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.barbearia.domain.enums.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de cadastro/atualização de funcionário.
 * 
 * @author Sua Barbearia Team
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class FuncionarioRequestDto {

        @JsonProperty("nome")
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        private String nome;

        @JsonProperty("email")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        private String email;

        @JsonProperty("telefone")
        @NotBlank(message = "Telefone é obrigatório")
        @Size(min = 10, max = 20, message = "Telefone deve ter entre 10 e 20 caracteres")
        private String telefone;

        @JsonProperty("perfilType")
        @NotNull(message = "Tipo de perfil é obrigatório")
        private TipoPerfil perfilType;
}

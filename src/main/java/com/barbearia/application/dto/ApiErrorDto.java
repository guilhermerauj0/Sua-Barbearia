package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto padrão para retorno de erros da API")
public class ApiErrorDto {

    @Schema(description = "Timestamp do erro", example = "2024-03-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Tipo do erro", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Dados inválidos fornecidos")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/api/agendamentos")
    private String path;
}

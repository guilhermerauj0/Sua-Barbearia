package com.barbearia.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AgendamentoReagendamentoDto(
    @NotNull(message = "Nova data e hora são obrigatórias")
    LocalDateTime novaDataHora
) {}

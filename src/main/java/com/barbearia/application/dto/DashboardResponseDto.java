package com.barbearia.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponseDto {
    private final Long funcionarioId;
    private final String nome;
    private final String email;
    private final String perfil;
    private final String linkInfo;
}

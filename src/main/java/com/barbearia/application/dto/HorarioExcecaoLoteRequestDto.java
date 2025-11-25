package com.barbearia.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class HorarioExcecaoLoteRequestDto {

    @NotEmpty(message = "Lista de exceções não pode estar vazia")
    @Valid
    private List<HorarioExcecaoRequestDto> excecoes;

    public HorarioExcecaoLoteRequestDto() {
    }

    public HorarioExcecaoLoteRequestDto(List<HorarioExcecaoRequestDto> excecoes) {
        this.excecoes = excecoes;
    }

    public List<HorarioExcecaoRequestDto> getExcecoes() {
        return excecoes;
    }

    public void setExcecoes(List<HorarioExcecaoRequestDto> excecoes) {
        this.excecoes = excecoes;
    }
}

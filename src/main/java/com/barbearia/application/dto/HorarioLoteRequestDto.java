package com.barbearia.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO para criação em lote de horários de funcionamento.
 * Permite que profissional defina todos os dias da semana de uma vez.
 */
public class HorarioLoteRequestDto {

    @NotNull(message = "Lista de horários não pode ser nula")
    @Size(min = 1, max = 7, message = "Deve conter entre 1 e 7 horários (um para cada dia da semana)")
    @Valid
    private List<HorarioFuncionamentoRequestDto> horarios;

    public HorarioLoteRequestDto() {
    }

    public HorarioLoteRequestDto(List<HorarioFuncionamentoRequestDto> horarios) {
        this.horarios = horarios;
    }

    // Getters e Setters

    public List<HorarioFuncionamentoRequestDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioFuncionamentoRequestDto> horarios) {
        this.horarios = horarios;
    }
}

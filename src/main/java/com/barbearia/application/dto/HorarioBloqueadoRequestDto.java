package com.barbearia.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para requisição de criação de bloqueio de horário.
 * 
 * Permite bloquear slots específicos (ex: almoço, reunião, pausa).
 */
public class HorarioBloqueadoRequestDto {

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Horário de início é obrigatório")
    private LocalTime horarioInicio;

    @NotNull(message = "Horário de fim é obrigatório")
    private LocalTime horarioFim;

    @Size(max = 255, message = "Motivo deve ter no máximo 255 caracteres")
    private String motivo; // Ex: "Almoço", "Reunião", "Pausa"

    public HorarioBloqueadoRequestDto() {
    }

    public HorarioBloqueadoRequestDto(LocalDate data, LocalTime horarioInicio,
            LocalTime horarioFim, String motivo) {
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.motivo = motivo;
    }

    // Getters e Setters

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

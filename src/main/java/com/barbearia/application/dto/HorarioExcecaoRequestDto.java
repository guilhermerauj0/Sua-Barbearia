package com.barbearia.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioExcecaoRequestDto {

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Hora de abertura é obrigatória")
    private LocalTime horaAbertura;

    @NotNull(message = "Hora de fechamento é obrigatória")
    private LocalTime horaFechamento;

    private String motivo;

    public HorarioExcecaoRequestDto() {
    }

    public HorarioExcecaoRequestDto(LocalDate data, LocalTime horaAbertura, LocalTime horaFechamento, String motivo) {
        this.data = data;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.motivo = motivo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraAbertura() {
        return horaAbertura;
    }

    public void setHoraAbertura(LocalTime horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public LocalTime getHoraFechamento() {
        return horaFechamento;
    }

    public void setHoraFechamento(LocalTime horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

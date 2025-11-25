package com.barbearia.application.dto;

/**
 * DTO para horário de pico no relatório.
 * 
 * Representa uma faixa horária com estatísticas de agendamentos:
 * - Faixa horária (ex: "09:00-10:00")
 * - Total de agendamentos nessa faixa
 * - Percentual em relação ao total de agendamentos
 * 
 * Útil para identificar horários mais movimentados e dimensionar equipe.
 * 
 * @author Sua Barbearia Team
 */
public class HorarioPicoDto {

    private String faixaHorario;
    private Integer totalAgendamentos;
    private Double percentual;

    public HorarioPicoDto() {
    }

    public HorarioPicoDto(String faixaHorario, Integer totalAgendamentos, Double percentual) {
        this.faixaHorario = faixaHorario;
        this.totalAgendamentos = totalAgendamentos;
        this.percentual = percentual;
    }

    // Getters and Setters

    public String getFaixaHorario() {
        return faixaHorario;
    }

    public void setFaixaHorario(String faixaHorario) {
        this.faixaHorario = faixaHorario;
    }

    public Integer getTotalAgendamentos() {
        return totalAgendamentos;
    }

    public void setTotalAgendamentos(Integer totalAgendamentos) {
        this.totalAgendamentos = totalAgendamentos;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }
}

package com.barbearia.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para Horário Disponível - representa um horário disponível para agendamento.
 */
public class HorarioDisponivelDto {
    
    private Long funcionarioId;
    private String funcionarioNome;
    private String profissao;
    private LocalDate data;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    
    public HorarioDisponivelDto() {
    }
    
    public HorarioDisponivelDto(Long funcionarioId, String funcionarioNome, String profissao,
                               LocalDate data, LocalTime horarioInicio, LocalTime horarioFim) {
        this.funcionarioId = funcionarioId;
        this.funcionarioNome = funcionarioNome;
        this.profissao = profissao;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }
    
    // Getters e Setters
    
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }
    
    public String getFuncionarioNome() {
        return funcionarioNome;
    }
    
    public void setFuncionarioNome(String funcionarioNome) {
        this.funcionarioNome = funcionarioNome;
    }
    
    public String getProfissao() {
        return profissao;
    }
    
    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }
    
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
}

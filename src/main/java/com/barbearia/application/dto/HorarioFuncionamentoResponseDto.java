package com.barbearia.application.dto;

import java.time.LocalTime;

/**
 * DTO para resposta de horário de funcionamento.
 * 
 * Retorna os dados do horário de funcionamento criado ou atualizado.
 */
public class HorarioFuncionamentoResponseDto {
    
    private Long id;
    private Long barbeariaId;
    private Integer diaSemana;
    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private Boolean ativo;
    
    public HorarioFuncionamentoResponseDto() {
    }
    
    public HorarioFuncionamentoResponseDto(Long id, Long barbeariaId, Integer diaSemana, 
                                           LocalTime horaAbertura, LocalTime horaFechamento, 
                                           Boolean ativo) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.ativo = ativo;
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getBarbeariaId() {
        return barbeariaId;
    }
    
    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }
    
    public Integer getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
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
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return "HorarioFuncionamentoResponseDto{" +
                "id=" + id +
                ", barbeariaId=" + barbeariaId +
                ", diaSemana=" + diaSemana +
                ", horaAbertura=" + horaAbertura +
                ", horaFechamento=" + horaFechamento +
                ", ativo=" + ativo +
                '}';
    }
}

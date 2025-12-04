package com.barbearia.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO para resposta de bloqueio de horário.
 * 
 * Retorna informações completas incluindo quem criou o bloqueio.
 */
public class HorarioBloqueadoResponseDto {

    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private LocalDate data;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String motivo;
    private String criadoPor; // "BARBEARIA" ou "PROFISSIONAL"
    private LocalDateTime dataCriacao;

    public HorarioBloqueadoResponseDto() {
    }

    public HorarioBloqueadoResponseDto(Long id, Long funcionarioId, String funcionarioNome,
            LocalDate data, LocalTime horarioInicio, LocalTime horarioFim,
            String motivo, String criadoPor, LocalDateTime dataCriacao) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.funcionarioNome = funcionarioNome;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.motivo = motivo;
        this.criadoPor = criadoPor;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}

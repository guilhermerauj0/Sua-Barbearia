package com.barbearia.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class HorarioExcecaoResponseDto {

    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private LocalDate data;
    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private String motivo;
    private String criadoPor;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    public HorarioExcecaoResponseDto() {
    }

    public HorarioExcecaoResponseDto(Long id, Long funcionarioId, String funcionarioNome, LocalDate data,
            LocalTime horaAbertura, LocalTime horaFechamento, String motivo, String criadoPor,
            Boolean ativo, LocalDateTime dataCriacao) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.funcionarioNome = funcionarioNome;
        this.data = data;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.motivo = motivo;
        this.criadoPor = criadoPor;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
    }

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

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}

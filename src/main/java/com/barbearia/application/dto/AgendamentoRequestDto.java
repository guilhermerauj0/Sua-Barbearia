package com.barbearia.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO para requisição de criação de agendamento
 * 
 * Recebe:
 * - servicoId: ID do serviço desejado
 * - funcionarioId: ID do profissional que executará o serviço
 * - dataHora: Data e hora desejada para o agendamento
 * - observacoes: Observações opcionais do cliente
 * 
 * @author Sua Barbearia Team
 */
public class AgendamentoRequestDto {
    
    @NotNull(message = "Service ID cannot be null")
    private Long servicoId;
    
    @NotNull(message = "Professional ID cannot be null")
    private Long funcionarioId;
    
    @NotNull(message = "Date and time cannot be null")
    private LocalDateTime dataHora;
    
    private String observacoes;
    
    // Construtores
    
    public AgendamentoRequestDto() {
    }
    
    public AgendamentoRequestDto(Long servicoId, Long funcionarioId, LocalDateTime dataHora) {
        this.servicoId = servicoId;
        this.funcionarioId = funcionarioId;
        this.dataHora = dataHora;
    }
    
    public AgendamentoRequestDto(Long servicoId, Long funcionarioId, LocalDateTime dataHora, String observacoes) {
        this.servicoId = servicoId;
        this.funcionarioId = funcionarioId;
        this.dataHora = dataHora;
        this.observacoes = observacoes;
    }
    
    // Getters e Setters
    
    public Long getServicoId() {
        return servicoId;
    }
    
    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }
    
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    @Override
    public String toString() {
        return "AgendamentoRequestDto{" +
                "servicoId=" + servicoId +
                ", funcionarioId=" + funcionarioId +
                ", dataHora=" + dataHora +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}

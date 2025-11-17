package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de criação de agendamento
 * 
 * Retorna:
 * - id: ID do agendamento criado
 * - clienteId: ID do cliente
 * - barbeariaId: ID da barbearia
 * - servicoId: ID do serviço
 * - funcionarioId: ID do profissional
 * - dataHora: Data e hora do agendamento
 * - status: Status do agendamento (PENDENTE, CONFIRMADO, CONCLUÍDO, CANCELADO)
 * - observacoes: Observações adicionais
 * - dataCriacao: Data de criação
 * - dataAtualizacao: Data da última atualização
 * 
 * @author Sua Barbearia Team
 */
public class AgendamentoResponseDto {
    
    private Long id;
    private Long clienteId;
    private Long barbeariaId;
    private Long servicoId;
    private Long funcionarioId;
    private LocalDateTime dataHora;
    private String status;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Construtores
    
    public AgendamentoResponseDto() {
    }
    
    public AgendamentoResponseDto(Long id, Long clienteId, Long barbeariaId, Long servicoId,
                                  Long funcionarioId, LocalDateTime dataHora, String status,
                                  String observacoes, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.barbeariaId = barbeariaId;
        this.servicoId = servicoId;
        this.funcionarioId = funcionarioId;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public Long getBarbeariaId() {
        return barbeariaId;
    }
    
    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    @Override
    public String toString() {
        return "AgendamentoResponseDto{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", barbeariaId=" + barbeariaId +
                ", servicoId=" + servicoId +
                ", funcionarioId=" + funcionarioId +
                ", dataHora=" + dataHora +
                ", status='" + status + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}

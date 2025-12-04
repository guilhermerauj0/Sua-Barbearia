package com.barbearia.domain.entities;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa um Agendamento.
 * 
 * Um agendamento relaciona:
 * - Cliente que solicitou o serviço
 * - Barbearia onde o serviço será realizado
 * - Barbeiro que executará o serviço (opcional inicialmente)
 * - Serviço a ser realizado
 * - Data e hora do agendamento
 * - Status atual do agendamento
 * 
 * Regras de negócio:
 * - Cliente e barbearia são obrigatórios
 * - Data e hora devem ser futuras no momento da criação
 * - Status inicial é PENDENTE
 * 
 * @author Sua Barbearia Team
 */
public class Agendamento {
    private Long id;
    private Long clienteId;
    private Long barbeariaId;
    private Long barbeiroId; // Opcional - pode ser atribuído depois
    private Long servicoId;
    private LocalDateTime dataHora;
    private StatusAgendamento status;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private boolean avaliado = false;

    /**
     * Construtor completo para criar um novo agendamento
     */
    public Agendamento(Long clienteId, Long barbeariaId, Long servicoId,
            LocalDateTime dataHora, String observacoes) {
        validarDadosObrigatorios(clienteId, barbeariaId, servicoId, dataHora);

        this.clienteId = clienteId;
        this.barbeariaId = barbeariaId;
        this.servicoId = servicoId;
        this.dataHora = dataHora;
        this.status = StatusAgendamento.PENDENTE;
        this.observacoes = observacoes;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Construtor completo (usado pelo mapper/repositório)
     */
    public Agendamento(Long id, Long clienteId, Long barbeariaId, Long barbeiroId,
            Long servicoId, LocalDateTime dataHora, StatusAgendamento status,
            String observacoes, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao,
            boolean avaliado) {
        this.id = id;
        this.clienteId = clienteId;
        this.barbeariaId = barbeariaId;
        this.barbeiroId = barbeiroId;
        this.servicoId = servicoId;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.avaliado = avaliado;
    }

    /**
     * Construtor vazio (para frameworks/JPA)
     */
    protected Agendamento() {
    }

    private void validarDadosObrigatorios(Long clienteId, Long barbeariaId,
            Long servicoId, LocalDateTime dataHora) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        if (barbeariaId == null) {
            throw new IllegalArgumentException("Barbearia é obrigatória");
        }
        if (servicoId == null) {
            throw new IllegalArgumentException("Serviço é obrigatório");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora são obrigatórias");
        }
    }

    // ==================== Métodos de Negócio ====================

    /**
     * Confirma o agendamento
     */
    public void confirmar() {
        if (this.status != StatusAgendamento.PENDENTE) {
            throw new IllegalStateException("Apenas agendamentos pendentes podem ser confirmados");
        }
        this.status = StatusAgendamento.CONFIRMADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Marca o agendamento como concluído
     */
    public void concluir() {
        if (this.status != StatusAgendamento.CONFIRMADO) {
            throw new IllegalStateException("Apenas agendamentos confirmados podem ser concluídos");
        }
        this.status = StatusAgendamento.CONCLUIDO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Cancela o agendamento
     */
    public void cancelar() {
        if (this.status == StatusAgendamento.CONCLUIDO) {
            throw new IllegalStateException("Agendamentos concluídos não podem ser cancelados");
        }
        this.status = StatusAgendamento.CANCELADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Atribui um barbeiro ao agendamento
     */
    public void atribuirBarbeiro(Long barbeiroId) {
        if (barbeiroId == null) {
            throw new IllegalArgumentException("ID do barbeiro não pode ser nulo");
        }
        this.barbeiroId = barbeiroId;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Verifica se o agendamento está no passado
     */
    public boolean ehPassado() {
        return this.dataHora.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica se o agendamento está no futuro
     */
    public boolean ehFuturo() {
        return this.dataHora.isAfter(LocalDateTime.now());
    }

    /**
     * Marca o cliente como faltou ao agendamento confirmado
     */
    public void marcarComoFaltou() {
        if (this.status != StatusAgendamento.CONFIRMADO) {
            throw new IllegalStateException("Apenas agendamentos confirmados podem ser marcados como faltou");
        }
        this.status = StatusAgendamento.FALTOU;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Marca o agendamento como avaliado
     */
    public void marcarComoAvaliado() {
        if (this.status != StatusAgendamento.CONCLUIDO) {
            throw new IllegalStateException("Apenas agendamentos concluídos podem ser avaliados");
        }
        this.avaliado = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ==================== Getters e Setters ====================

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

    public Long getBarbeiroId() {
        return barbeiroId;
    }

    public void setBarbeiroId(Long barbeiroId) {
        this.barbeiroId = barbeiroId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
        this.dataAtualizacao = LocalDateTime.now();
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

    public boolean isAvaliado() {
        return avaliado;
    }

    public void setAvaliado(boolean avaliado) {
        this.avaliado = avaliado;
    }

    // ==================== equals, hashCode e toString ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Agendamento that = (Agendamento) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", barbeariaId=" + barbeariaId +
                ", barbeiroId=" + barbeiroId +
                ", servicoId=" + servicoId +
                ", dataHora=" + dataHora +
                ", status=" + status +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}

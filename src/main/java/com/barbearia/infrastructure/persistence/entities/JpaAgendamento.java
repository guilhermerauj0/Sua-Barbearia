package com.barbearia.infrastructure.persistence.entities;

import com.barbearia.domain.enums.StatusAgendamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa a tabela de Agendamentos no banco de dados.
 * 
 * Relacionamentos:
 * - ManyToOne com JpaCliente: vários agendamentos para um cliente
 * - ManyToOne com JpaBarbearia: vários agendamentos para uma barbearia
 * - ManyToOne com JpaBarbeiro (futuro): vários agendamentos para um barbeiro
 * - ManyToOne com JpaServico (futuro): vários agendamentos para um serviço
 * 
 * Por enquanto, apenas armazenamos IDs das entidades relacionadas.
 * Relacionamentos completos serão implementados quando essas entidades
 * existirem.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "agendamentos", indexes = {
        @Index(name = "idx_cliente_datahora", columnList = "cliente_id, data_hora"),
        @Index(name = "idx_barbearia_datahora", columnList = "barbearia_id, data_hora"),
        @Index(name = "idx_status", columnList = "status")
})
public class JpaAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID do cliente que fez o agendamento
     * Por enquanto apenas FK, relacionamento completo virá depois
     */
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /**
     * ID da barbearia onde será realizado o serviço
     */
    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;

    /**
     * ID do barbeiro que executará o serviço (opcional)
     * Pode ser atribuído depois da criação do agendamento
     */
    @Column(name = "barbeiro_id")
    private Long barbeiroId;

    /**
     * ID do serviço a ser realizado
     */
    @Column(name = "servico_id", nullable = false)
    private Long servicoId;

    /**
     * Data e hora agendadas para o serviço
     */
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    /**
     * Status atual do agendamento
     * Armazenado como STRING no banco
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status;

    /**
     * Observações adicionais do cliente
     */
    @Column(length = 500)
    private String observacoes;

    /**
     * Data e hora de criação do registro
     * Preenchida automaticamente
     */
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data e hora da última atualização
     * Atualizada automaticamente
     */
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    /**
     * Indica se o agendamento já foi avaliado pelo cliente
     * Previne avaliações duplicadas
     */
    @Column(nullable = false)
    private boolean avaliado = false;

    /**
     * Callback executado antes de persistir a entidade
     * Define dataCriacao e dataAtualizacao
     */
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (status == null) {
            status = StatusAgendamento.PENDENTE;
        }
    }

    /**
     * Callback executado antes de atualizar a entidade
     * Atualiza dataAtualizacao
     */
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // ==================== Construtor vazio (exigido pelo JPA) ====================

    public JpaAgendamento() {
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
}

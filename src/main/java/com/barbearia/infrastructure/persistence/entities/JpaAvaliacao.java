package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA para Avaliações de Barbearias.
 * 
 * Mapeia tabela 'avaliacoes' criada pela migration V5.
 */
@Entity
@Table(name = "avaliacoes")
public class JpaAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;

    @Column(name = "agendamento_id")
    private Long agendamentoId;

    @Column(name = "nota_servico", nullable = false)
    private Integer notaServico;

    @Column(name = "nota_ambiente", nullable = false)
    private Integer notaAmbiente;

    @Column(name = "nota_limpeza", nullable = false)
    private Integer notaLimpeza;

    @Column(name = "nota_atendimento", nullable = false)
    private Integer notaAtendimento;

    @Column(name = "nota_geral", nullable = false, precision = 3, scale = 2)
    private java.math.BigDecimal notaGeral;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        // Calcula nota geral antes de persistir
        if (notaServico != null && notaAmbiente != null && notaLimpeza != null && notaAtendimento != null) {
            notaGeral = new BigDecimal(notaServico + notaAmbiente + notaLimpeza + notaAtendimento)
                    .divide(new BigDecimal(4), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Constructors

    public JpaAvaliacao() {
    }

    public JpaAvaliacao(Long clienteId, Long barbeariaId, Long agendamentoId,
            Integer notaServico, Integer notaAmbiente, Integer notaLimpeza,
            Integer notaAtendimento, String comentario) {
        this.clienteId = clienteId;
        this.barbeariaId = barbeariaId;
        this.agendamentoId = agendamentoId;
        this.notaServico = notaServico;
        this.notaAmbiente = notaAmbiente;
        this.notaLimpeza = notaLimpeza;
        this.notaAtendimento = notaAtendimento;
        this.comentario = comentario;
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

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(Long agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public Integer getNotaServico() {
        return notaServico;
    }

    public void setNotaServico(Integer notaServico) {
        this.notaServico = notaServico;
    }

    public Integer getNotaAmbiente() {
        return notaAmbiente;
    }

    public void setNotaAmbiente(Integer notaAmbiente) {
        this.notaAmbiente = notaAmbiente;
    }

    public Integer getNotaLimpeza() {
        return notaLimpeza;
    }

    public void setNotaLimpeza(Integer notaLimpeza) {
        this.notaLimpeza = notaLimpeza;
    }

    public Integer getNotaAtendimento() {
        return notaAtendimento;
    }

    public void setNotaAtendimento(Integer notaAtendimento) {
        this.notaAtendimento = notaAtendimento;
    }

    public java.math.BigDecimal getNotaGeral() {
        return notaGeral;
    }

    public void setNotaGeral(java.math.BigDecimal notaGeral) {
        this.notaGeral = notaGeral;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}

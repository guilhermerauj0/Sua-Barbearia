package com.barbearia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de avaliação (dados públicos).
 * 
 * Retorna avaliação completa sem expor informações sensíveis do cliente (LGPD).
 */
public class AvaliacaoResponseDto {

    private Long id;
    private Long barbeariaId;
    private Long agendamentoId;
    private String clienteNome; // Apenas nome, não email/telefone

    // Notas individuais
    private Integer notaServico;
    private Integer notaAmbiente;
    private Integer notaLimpeza;
    private Integer notaAtendimento;

    // Nota geral calculada
    private BigDecimal notaGeral;

    private String comentario;
    private LocalDateTime dataCriacao;

    public AvaliacaoResponseDto() {
    }

    public AvaliacaoResponseDto(Long id, Long barbeariaId, Long agendamentoId,
            String clienteNome, Integer notaServico, Integer notaAmbiente,
            Integer notaLimpeza, Integer notaAtendimento, BigDecimal notaGeral,
            String comentario, LocalDateTime dataCriacao) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.agendamentoId = agendamentoId;
        this.clienteNome = clienteNome;
        this.notaServico = notaServico;
        this.notaAmbiente = notaAmbiente;
        this.notaLimpeza = notaLimpeza;
        this.notaAtendimento = notaAtendimento;
        this.notaGeral = notaGeral;
        this.comentario = comentario;
        this.dataCriacao = dataCriacao;
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

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(Long agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
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

    public BigDecimal getNotaGeral() {
        return notaGeral;
    }

    public void setNotaGeral(BigDecimal notaGeral) {
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

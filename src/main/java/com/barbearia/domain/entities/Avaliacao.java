package com.barbearia.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio para Avaliação de Barbearia.
 * 
 * Representa uma avaliação multi-aspecto feita por um cliente sobre uma
 * barbearia.
 * Sistema de notas em 4 dimensões com cálculo automático de média geral.
 * 
 * Princípios POO aplicados:
 * - Encapsulamento: atributos privados com getters/setters
 * - Responsabilidade Única: gerencia apenas dados e validações de avaliação
 * - Invariantes de domínio: notas entre 1-5, cálculo automático da média
 * 
 * @author Sua Barbearia Team
 */
public class Avaliacao {

    private Long id;
    private Long clienteId;
    private Long barbeariaId;
    private Long agendamentoId;

    // Notas individuais (1-5)
    private Integer notaServico;
    private Integer notaAmbiente;
    private Integer notaLimpeza;
    private Integer notaAtendimento;

    // Calculada automaticamente
    private Double notaGeral;

    private String comentario;
    private LocalDateTime dataCriacao;

    public Avaliacao() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Avaliacao(Long clienteId, Long barbeariaId, Long agendamentoId,
            Integer notaServico, Integer notaAmbiente,
            Integer notaLimpeza, Integer notaAtendimento,
            String comentario) {
        this.clienteId = clienteId;
        this.barbeariaId = barbeariaId;
        this.agendamentoId = agendamentoId;
        this.notaServico = notaServico;
        this.notaAmbiente = notaAmbiente;
        this.notaLimpeza = notaLimpeza;
        this.notaAtendimento = notaAtendimento;
        this.comentario = comentario;
        this.dataCriacao = LocalDateTime.now();
        validate();
        calcularNotaGeral();
    }

    /**
     * Valida as regras de negócio da avaliação.
     * 
     * @throws IllegalArgumentException se dados inválidos
     */
    public void validate() {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }

        if (barbeariaId == null) {
            throw new IllegalArgumentException("Barbearia é obrigatória");
        }

        if (agendamentoId == null) {
            throw new IllegalArgumentException("Agendamento é obrigatório");
        }

        validarNota(notaServico, "Nota do serviço");
        validarNota(notaAmbiente, "Nota do ambiente");
        validarNota(notaLimpeza, "Nota da limpeza");
        validarNota(notaAtendimento, "Nota do atendimento");
    }

    /**
     * Valida se uma nota individual está no range válido (1-5).
     */
    private void validarNota(Integer nota, String campo) {
        if (nota == null) {
            throw new IllegalArgumentException(campo + " é obrigatória");
        }

        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException(campo + " deve estar entre 1 e 5");
        }
    }

    /**
     * Calcula a nota geral como média aritmética das 4 notas individuais.
     * Método de negócio do domínio.
     */
    public void calcularNotaGeral() {
        if (notaServico == null || notaAmbiente == null ||
                notaLimpeza == null || notaAtendimento == null) {
            throw new IllegalStateException("Todas as notas devem estar preenchidas para calcular a média");
        }

        this.notaGeral = (notaServico + notaAmbiente + notaLimpeza + notaAtendimento) / 4.0;
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
        if (notaServico != null && notaAmbiente != null &&
                notaLimpeza != null && notaAtendimento != null) {
            calcularNotaGeral();
        }
    }

    public Integer getNotaAmbiente() {
        return notaAmbiente;
    }

    public void setNotaAmbiente(Integer notaAmbiente) {
        this.notaAmbiente = notaAmbiente;
        if (notaServico != null && notaAmbiente != null &&
                notaLimpeza != null && notaAtendimento != null) {
            calcularNotaGeral();
        }
    }

    public Integer getNotaLimpeza() {
        return notaLimpeza;
    }

    public void setNotaLimpeza(Integer notaLimpeza) {
        this.notaLimpeza = notaLimpeza;
        if (notaServico != null && notaAmbiente != null &&
                notaLimpeza != null && notaAtendimento != null) {
            calcularNotaGeral();
        }
    }

    public Integer getNotaAtendimento() {
        return notaAtendimento;
    }

    public void setNotaAtendimento(Integer notaAtendimento) {
        this.notaAtendimento = notaAtendimento;
        if (notaServico != null && notaAmbiente != null &&
                notaLimpeza != null && notaAtendimento != null) {
            calcularNotaGeral();
        }
    }

    public Double getNotaGeral() {
        return notaGeral;
    }

    public void setNotaGeral(Double notaGeral) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", barbeariaId=" + barbeariaId +
                ", agendamentoId=" + agendamentoId +
                ", notaGeral=" + notaGeral +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}

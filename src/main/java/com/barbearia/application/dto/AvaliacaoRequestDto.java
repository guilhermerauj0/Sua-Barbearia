package com.barbearia.application.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para requisição de criação de avaliação multi-aspecto.
 * 
 * Sistema de 4 notas independentes + comentário opcional.
 * Todas as 4 notas são obrigatórias para garantir avaliação completa.
 */
public class AvaliacaoRequestDto {

    @NotNull(message = "Barbearia é obrigatória")
    private Long barbeariaId;

    @NotNull(message = "Agendamento é obrigatório")
    private Long agendamentoId;

    // Notas individuais (todas obrigatórias, 1-5)
    @NotNull(message = "Nota do serviço é obrigatória")
    @Min(value = 1, message = "Nota do serviço deve ser no mínimo 1")
    @Max(value = 5, message = "Nota do serviço deve ser no máximo 5")
    private Integer notaServico;

    @NotNull(message = "Nota do ambiente é obrigatória")
    @Min(value = 1, message = "Nota do ambiente deve ser no mínimo 1")
    @Max(value = 5, message = "Nota do ambiente deve ser no máximo 5")
    private Integer notaAmbiente;

    @NotNull(message = "Nota da limpeza é obrigatória")
    @Min(value = 1, message = "Nota da limpeza deve ser no mínimo 1")
    @Max(value = 5, message = "Nota da limpeza deve ser no máximo 5")
    private Integer notaLimpeza;

    @NotNull(message = "Nota do atendimento é obrigatória")
    @Min(value = 1, message = "Nota do atendimento deve ser no mínimo 1")
    @Max(value = 5, message = "Nota do atendimento deve ser no máximo 5")
    private Integer notaAtendimento;

    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    private String comentario;

    public AvaliacaoRequestDto() {
    }

    // Getters e Setters

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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

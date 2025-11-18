package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoExcecao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para resposta de exceção de horário (feriado, fechamento especial, etc).
 */
@Schema(description = "Dados de exceção de horário")
public class FeriadoExcecaoResponseDto {
    
    @Schema(description = "ID da exceção", example = "1")
    private Long id;
    
    @Schema(description = "ID da barbearia", example = "1")
    private Long barbeariaId;
    
    @Schema(description = "Data da exceção", example = "2024-12-25")
    private LocalDate data;
    
    @Schema(description = "Tipo de exceção (FECHADO ou HORARIO_ESPECIAL)", example = "FECHADO")
    private TipoExcecao tipo;
    
    @Schema(description = "Horário de abertura (se tipo for HORARIO_ESPECIAL)", example = "10:00:00")
    private LocalTime horarioAbertura;
    
    @Schema(description = "Horário de fechamento (se tipo for HORARIO_ESPECIAL)", example = "14:00:00")
    private LocalTime horarioFechamento;
    
    @Schema(description = "Descrição da exceção", example = "Natal - Fechado")
    private String descricao;
    
    @Schema(description = "Se a exceção está ativa", example = "true")
    private boolean ativo;

    public FeriadoExcecaoResponseDto() {
    }

    public FeriadoExcecaoResponseDto(Long id, Long barbeariaId, LocalDate data, TipoExcecao tipo,
                                     LocalTime horarioAbertura, LocalTime horarioFechamento, 
                                     String descricao, boolean ativo) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.data = data;
        this.tipo = tipo;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.descricao = descricao;
        this.ativo = ativo;
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoExcecao getTipo() {
        return tipo;
    }

    public void setTipo(TipoExcecao tipo) {
        this.tipo = tipo;
    }

    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

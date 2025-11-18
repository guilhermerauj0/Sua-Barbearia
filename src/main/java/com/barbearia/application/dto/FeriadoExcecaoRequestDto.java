package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoExcecao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para requisição de criação/atualização de exceção de horário (feriado, fechamento especial, etc).
 */
@Schema(description = "Dados para criar ou atualizar exceção de horário")
public class FeriadoExcecaoRequestDto {
    
    @Schema(description = "Data da exceção", example = "2024-12-25")
    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
    
    @Schema(description = "Tipo de exceção (FECHADO ou HORARIO_ESPECIAL)", example = "FECHADO")
    @NotNull(message = "Tipo de exceção é obrigatório")
    private TipoExcecao tipo;
    
    @Schema(description = "Horário de abertura (obrigatório se tipo for HORARIO_ESPECIAL)", example = "10:00:00")
    private LocalTime horarioAbertura;
    
    @Schema(description = "Horário de fechamento (obrigatório se tipo for HORARIO_ESPECIAL)", example = "14:00:00")
    private LocalTime horarioFechamento;
    
    @Schema(description = "Descrição da exceção", example = "Natal - Fechado")
    private String descricao;
    
    @Schema(description = "Se a exceção está ativa", example = "true", defaultValue = "true")
    private Boolean ativo = true;

    public FeriadoExcecaoRequestDto() {
    }

    public FeriadoExcecaoRequestDto(LocalDate data, TipoExcecao tipo, String descricao) {
        this.data = data;
        this.tipo = tipo;
        this.descricao = descricao;
        this.ativo = true;
    }

    public FeriadoExcecaoRequestDto(LocalDate data, TipoExcecao tipo, LocalTime horarioAbertura, 
                                    LocalTime horarioFechamento, String descricao) {
        this.data = data;
        this.tipo = tipo;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.descricao = descricao;
        this.ativo = true;
    }

    // Getters e Setters

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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}

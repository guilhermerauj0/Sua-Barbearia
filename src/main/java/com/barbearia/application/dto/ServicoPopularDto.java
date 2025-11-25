package com.barbearia.application.dto;

import java.math.BigDecimal;

/**
 * DTO para serviço popular no relatório.
 * 
 * Representa um serviço com suas estatísticas:
 * - Identificador e nome do serviço
 * - Total de vezes que foi agendado
 * - Receita total gerada pelo serviço
 * 
 * Usado para ranking de serviços mais populares.
 * 
 * @author Sua Barbearia Team
 */
public class ServicoPopularDto {

    private Long servicoId;
    private String servicoNome;
    private Integer totalAgendamentos;
    private BigDecimal receitaTotal;

    public ServicoPopularDto() {
    }

    public ServicoPopularDto(Long servicoId, String servicoNome,
            Integer totalAgendamentos, BigDecimal receitaTotal) {
        this.servicoId = servicoId;
        this.servicoNome = servicoNome;
        this.totalAgendamentos = totalAgendamentos;
        this.receitaTotal = receitaTotal;
    }

    // Getters and Setters

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public String getServicoNome() {
        return servicoNome;
    }

    public void setServicoNome(String servicoNome) {
        this.servicoNome = servicoNome;
    }

    public Integer getTotalAgendamentos() {
        return totalAgendamentos;
    }

    public void setTotalAgendamentos(Integer totalAgendamentos) {
        this.totalAgendamentos = totalAgendamentos;
    }

    public BigDecimal getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(BigDecimal receitaTotal) {
        this.receitaTotal = receitaTotal;
    }
}

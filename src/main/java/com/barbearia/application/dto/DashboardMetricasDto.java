package com.barbearia.application.dto;

import java.math.BigDecimal;

/**
 * DTO para métricas do dashboard da barbearia.
 * 
 * Contém estatísticas gerais para visão rápida do negócio:
 * - Total de clientes únicos
 * - Agendamentos do mês atual
 * - Receita média por agendamento
 * - Taxa de cancelamento
 * 
 * @author Sua Barbearia Team
 */
public class DashboardMetricasDto {

    private Integer totalClientes;
    private Integer agendamentosMes;
    private BigDecimal receitaMedia;
    private Double taxaCancelamento;

    public DashboardMetricasDto() {
    }

    public DashboardMetricasDto(Integer totalClientes, Integer agendamentosMes,
            BigDecimal receitaMedia, Double taxaCancelamento) {
        this.totalClientes = totalClientes;
        this.agendamentosMes = agendamentosMes;
        this.receitaMedia = receitaMedia;
        this.taxaCancelamento = taxaCancelamento;
    }

    // Getters and Setters

    public Integer getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Integer totalClientes) {
        this.totalClientes = totalClientes;
    }

    public Integer getAgendamentosMes() {
        return agendamentosMes;
    }

    public void setAgendamentosMes(Integer agendamentosMes) {
        this.agendamentosMes = agendamentosMes;
    }

    public BigDecimal getReceitaMedia() {
        return receitaMedia;
    }

    public void setReceitaMedia(BigDecimal receitaMedia) {
        this.receitaMedia = receitaMedia;
    }

    public Double getTaxaCancelamento() {
        return taxaCancelamento;
    }

    public void setTaxaCancelamento(Double taxaCancelamento) {
        this.taxaCancelamento = taxaCancelamento;
    }
}

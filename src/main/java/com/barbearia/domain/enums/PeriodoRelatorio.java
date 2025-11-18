package com.barbearia.domain.enums;

/**
 * Enumeração que representa os períodos disponíveis para relatórios financeiros.
 * 
 * <p>Períodos suportados:</p>
 * <ul>
 *   <li><b>DIA:</b> Relatório das últimas 24 horas</li>
 *   <li><b>SEMANA:</b> Relatório dos últimos 7 dias</li>
 *   <li><b>MES:</b> Relatório dos últimos 30 dias</li>
 * </ul>
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
public enum PeriodoRelatorio {
    /**
     * Relatório do dia atual (últimas 24 horas).
     */
    DIA,
    
    /**
     * Relatório da semana atual (últimos 7 dias).
     */
    SEMANA,
    
    /**
     * Relatório do mês atual (últimos 30 dias).
     */
    MES;
    
    /**
     * Retorna o número de dias correspondente ao período.
     * 
     * @return Número de dias do período
     */
    public int getDias() {
        return switch (this) {
            case DIA -> 1;
            case SEMANA -> 7;
            case MES -> 30;
        };
    }
    
    /**
     * Retorna uma descrição amigável do período.
     * 
     * @return Descrição do período
     */
    public String getDescricao() {
        return switch (this) {
            case DIA -> "Último dia (24 horas)";
            case SEMANA -> "Última semana (7 dias)";
            case MES -> "Último mês (30 dias)";
        };
    }
}

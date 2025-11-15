package com.barbearia.application.dto;

import java.time.LocalTime;

/**
 * DTO para requisição de criação de horário de funcionamento.
 * 
 * Representa os horários de funcionamento da barbearia por dia da semana.
 * 
 * Validações:
 * - diaSemana deve estar entre 0 (domingo) e 6 (sábado)
 * - horaAbertura e horaFechamento são obrigatórios
 * - horaAbertura deve ser antes de horaFechamento
 */
public class HorarioFuncionamentoRequestDto {
    
    /**
     * Dia da semana (0 = domingo, 1 = segunda, ..., 6 = sábado)
     */
    private Integer diaSemana;
    
    /**
     * Hora de abertura da barbearia
     */
    private LocalTime horaAbertura;
    
    /**
     * Hora de fechamento da barbearia
     */
    private LocalTime horaFechamento;
    
    public HorarioFuncionamentoRequestDto() {
    }
    
    public HorarioFuncionamentoRequestDto(Integer diaSemana, LocalTime horaAbertura, LocalTime horaFechamento) {
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
    }
    
    // Validações
    
    /**
     * Valida se o DTO contém dados válidos
     * 
     * @return true se válido, false caso contrário
     */
    public boolean isValid() {
        if (diaSemana == null || diaSemana < 0 || diaSemana > 6) {
            return false;
        }
        
        if (horaAbertura == null || horaFechamento == null) {
            return false;
        }
        
        if (!horaAbertura.isBefore(horaFechamento)) {
            return false;
        }
        
        return true;
    }
    
    // Getters e Setters
    
    public Integer getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }
    
    public LocalTime getHoraAbertura() {
        return horaAbertura;
    }
    
    public void setHoraAbertura(LocalTime horaAbertura) {
        this.horaAbertura = horaAbertura;
    }
    
    public LocalTime getHoraFechamento() {
        return horaFechamento;
    }
    
    public void setHoraFechamento(LocalTime horaFechamento) {
        this.horaFechamento = horaFechamento;
    }
    
    @Override
    public String toString() {
        return "HorarioFuncionamentoRequestDto{" +
                "diaSemana=" + diaSemana +
                ", horaAbertura=" + horaAbertura +
                ", horaFechamento=" + horaFechamento +
                '}';
    }
}

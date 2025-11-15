package com.barbearia.domain.entities;

import java.time.LocalTime;

/**
 * Representa o horário de funcionamento de uma barbearia por dia da semana.
 * 
 * Cada barbearia pode ter diferentes horários para cada dia da semana.
 * Exemplo: Segunda-sexta 08:00-19:00, Sábado 08:00-15:00, Domingo fechado.
 * 
 * Conceitos POO:
 * - Encapsulamento: atributos privados com getters/setters
 * - Responsabilidade única: apenas gerencia horários de funcionamento
 * 
 * @author Sua Barbearia Team
 */
public class HorarioFuncionamento {
    
    private Long id;
    private Long barbeariaId;
    private Integer diaSemana; // 0=Domingo, 1=Segunda, ..., 6=Sábado
    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private boolean ativo;
    
    public HorarioFuncionamento() {
    }
    
    public HorarioFuncionamento(Long barbeariaId, Integer diaSemana, LocalTime horaAbertura, LocalTime horaFechamento) {
        this.barbeariaId = barbeariaId;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.ativo = true;
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
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    /**
     * Verifica se um horário está dentro do período de funcionamento.
     * 
     * @param horario horário a verificar
     * @return true se o horário está dentro do período de funcionamento
     */
    public boolean contemHorario(LocalTime horario) {
        return !horario.isBefore(horaAbertura) && !horario.isAfter(horaFechamento);
    }
}

package com.barbearia.domain.entities;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa o horário de funcionamento de uma barbearia por dia da semana.
 * 
 * Cada barbearia pode ter diferentes horários para cada dia da semana.
 * Exemplo: Segunda-sexta 08:00-19:00, Sábado 08:00-15:00, Domingo fechado.
 * 
 * Conceitos POO:
 * - Encapsulamento: atributos privados com getters/setters
 * - Responsabilidade única: apenas gerencia horários de funcionamento
 * - Validações de regras de negócio no próprio domínio
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
        this.ativo = true;
    }
    
    public HorarioFuncionamento(Long barbeariaId, Integer diaSemana, LocalTime horaAbertura, LocalTime horaFechamento) {
        this.barbeariaId = barbeariaId;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.ativo = true;
        validate();
    }
    
    /**
     * Valida a consistência dos horários de funcionamento.
     * 
     * @throws IllegalArgumentException se os horários forem inválidos
     */
    public void validate() {
        if (barbeariaId == null) {
            throw new IllegalArgumentException("BarbeariaId não pode ser nulo");
        }
        
        if (diaSemana == null || diaSemana < 0 || diaSemana > 6) {
            throw new IllegalArgumentException("Dia da semana deve estar entre 0 (Domingo) e 6 (Sábado)");
        }
        
        if (horaAbertura == null) {
            throw new IllegalArgumentException("Horário de abertura não pode ser nulo");
        }
        
        if (horaFechamento == null) {
            throw new IllegalArgumentException("Horário de fechamento não pode ser nulo");
        }
        
        if (horaFechamento.isBefore(horaAbertura) || horaFechamento.equals(horaAbertura)) {
            throw new IllegalArgumentException(
                "Horário de fechamento deve ser posterior ao horário de abertura"
            );
        }
    }
    
    /**
     * Verifica se há sobreposição com outro horário de funcionamento.
     * 
     * @param outro outro horário de funcionamento
     * @return true se houver sobreposição
     */
    public boolean sobrepoe(HorarioFuncionamento outro) {
        if (outro == null || !this.diaSemana.equals(outro.diaSemana) || !this.ativo || !outro.ativo) {
            return false;
        }
        
        // Verifica se os intervalos se sobrepõem
        return this.horaAbertura.isBefore(outro.horaFechamento) 
            && outro.horaAbertura.isBefore(this.horaFechamento);
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
        validate();
    }
    
    public Integer getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
        validate();
    }
    
    public LocalTime getHoraAbertura() {
        return horaAbertura;
    }
    
    public void setHoraAbertura(LocalTime horaAbertura) {
        this.horaAbertura = horaAbertura;
        validate();
    }
    
    public LocalTime getHoraFechamento() {
        return horaFechamento;
    }
    
    public void setHoraFechamento(LocalTime horaFechamento) {
        this.horaFechamento = horaFechamento;
        validate();
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
        return ativo && !horario.isBefore(horaAbertura) && !horario.isAfter(horaFechamento);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioFuncionamento that = (HorarioFuncionamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HorarioFuncionamento{" +
                "id=" + id +
                ", barbeariaId=" + barbeariaId +
                ", diaSemana=" + diaSemana +
                ", horaAbertura=" + horaAbertura +
                ", horaFechamento=" + horaFechamento +
                ", ativo=" + ativo +
                '}';
    }
}

package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidade JPA para persistência de horários de funcionamento da barbearia.
 */
@Entity
@Table(name = "horarios_funcionamento", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"barbearia_id", "dia_semana", "hora_abertura"}))
public class HorarioFuncionamentoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;
    
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 0=Domingo, 1=Segunda, ..., 6=Sábado
    
    @Column(name = "hora_abertura", nullable = false)
    private LocalTime horaAbertura;
    
    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime horaFechamento;
    
    @Column(nullable = false)
    private boolean ativo = true;

    public HorarioFuncionamentoEntity() {
    }

    public HorarioFuncionamentoEntity(Long barbeariaId, Integer diaSemana, 
                                      LocalTime horaAbertura, LocalTime horaFechamento) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioFuncionamentoEntity that = (HorarioFuncionamentoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HorarioFuncionamentoEntity{" +
                "id=" + id +
                ", barbeariaId=" + barbeariaId +
                ", diaSemana=" + diaSemana +
                ", horaAbertura=" + horaAbertura +
                ", horaFechamento=" + horaFechamento +
                ", ativo=" + ativo +
                '}';
    }
}

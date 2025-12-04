package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Entidade JPA para HorarioFuncionamento (horário de funcionamento por dia da semana).
 * Dia da semana: 1=SEGUNDA, 2=TERÇA, ..., 7=DOMINGO
 */
@Entity
@Table(name = "horarios_funcionamento", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"barbearia_id", "dia_semana", "funcionario_id"})
})
public class JpaHorarioFuncionamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;

    @Column(name = "funcionario_id")
    private Long funcionarioId;
    
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;  // 1=SEGUNDA até 7=DOMINGO (ISO 8601)
    
    @Column(name = "hora_abertura", nullable = false)
    private LocalTime horaAbertura;
    
    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime horaFechamento;
    
    @Column(nullable = false)
    private boolean ativo;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    public JpaHorarioFuncionamento() {
    }
    
    public JpaHorarioFuncionamento(Long barbeariaId, Integer diaSemana, 
                                    LocalTime horaAbertura, LocalTime horaFechamento) {
        this.barbeariaId = barbeariaId;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public JpaHorarioFuncionamento(Long barbeariaId, Long funcionarioId, Integer diaSemana, 
                                    LocalTime horaAbertura, LocalTime horaFechamento) {
        this.barbeariaId = barbeariaId;
        this.funcionarioId = funcionarioId;
        this.diaSemana = diaSemana;
        this.horaAbertura = horaAbertura;
        this.horaFechamento = horaFechamento;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    /**
     * Verifica se uma hora específica está dentro do horário de funcionamento.
     */
    public boolean contemHorario(LocalTime horario) {
        if (horario == null) {
            return false;
        }
        return !horario.isBefore(horaAbertura) && horario.isBefore(horaFechamento);
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
    
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
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
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}

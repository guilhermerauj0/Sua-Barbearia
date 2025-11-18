package com.barbearia.infrastructure.persistence.entities;

import com.barbearia.domain.enums.TipoExcecao;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidade JPA para persistência de exceções de horário (feriados, fechamentos especiais).
 */
@Entity
@Table(name = "feriados_excecoes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"barbearia_id", "data"}))
public class FeriadoExcecaoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;
    
    @Column(nullable = false)
    private LocalDate data;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoExcecao tipo;
    
    @Column(name = "horario_abertura")
    private LocalTime horarioAbertura;
    
    @Column(name = "horario_fechamento")
    private LocalTime horarioFechamento;
    
    @Column(length = 255)
    private String descricao;
    
    @Column(nullable = false)
    private boolean ativo = true;

    public FeriadoExcecaoEntity() {
    }

    public FeriadoExcecaoEntity(Long barbeariaId, LocalDate data, TipoExcecao tipo, String descricao) {
        this.barbeariaId = barbeariaId;
        this.data = data;
        this.tipo = tipo;
        this.descricao = descricao;
        this.ativo = true;
    }

    public FeriadoExcecaoEntity(Long barbeariaId, LocalDate data, TipoExcecao tipo,
                                LocalTime horarioAbertura, LocalTime horarioFechamento, String descricao) {
        this.barbeariaId = barbeariaId;
        this.data = data;
        this.tipo = tipo;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.descricao = descricao;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeriadoExcecaoEntity that = (FeriadoExcecaoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FeriadoExcecaoEntity{" +
                "id=" + id +
                ", barbeariaId=" + barbeariaId +
                ", data=" + data +
                ", tipo=" + tipo +
                ", horarioAbertura=" + horarioAbertura +
                ", horarioFechamento=" + horarioFechamento +
                ", descricao='" + descricao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}

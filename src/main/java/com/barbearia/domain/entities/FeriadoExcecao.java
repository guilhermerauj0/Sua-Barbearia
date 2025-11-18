package com.barbearia.domain.entities;

import com.barbearia.domain.enums.TipoExcecao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa exceções aos horários normais de funcionamento.
 * Útil para feriados, fechamentos especiais, eventos ou horários diferenciados.
 */
public class FeriadoExcecao {
    
    private Long id;
    private Long barbeariaId;
    private LocalDate data;
    private TipoExcecao tipo;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private String descricao;
    private boolean ativo;

    public FeriadoExcecao() {
        this.ativo = true;
    }

    public FeriadoExcecao(Long id, Long barbeariaId, LocalDate data, TipoExcecao tipo,
                          LocalTime horarioAbertura, LocalTime horarioFechamento, String descricao) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.data = data;
        this.tipo = tipo;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.descricao = descricao;
        this.ativo = true;
        validate();
    }

    /**
     * Valida a consistência da exceção de horário.
     * 
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public void validate() {
        if (barbeariaId == null) {
            throw new IllegalArgumentException("BarbeariaId não pode ser nulo");
        }
        
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula");
        }
        
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de exceção não pode ser nulo");
        }
        
        // Se for HORARIO_ESPECIAL, deve ter horários definidos
        if (tipo == TipoExcecao.HORARIO_ESPECIAL) {
            if (horarioAbertura == null || horarioFechamento == null) {
                throw new IllegalArgumentException(
                    "Horário especial deve ter horário de abertura e fechamento definidos"
                );
            }
            
            if (horarioFechamento.isBefore(horarioAbertura) || horarioFechamento.equals(horarioAbertura)) {
                throw new IllegalArgumentException(
                    "Horário de fechamento deve ser posterior ao horário de abertura"
                );
            }
        }
        
        // Se for FECHADO, não deve ter horários
        if (tipo == TipoExcecao.FECHADO) {
            if (horarioAbertura != null || horarioFechamento != null) {
                throw new IllegalArgumentException(
                    "Exceção de fechamento não deve ter horários definidos"
                );
            }
        }
    }

    /**
     * Verifica se a barbearia está aberta em um horário específico nesta data de exceção.
     * 
     * @param horario o horário a ser verificado
     * @return true se estiver aberta
     */
    public boolean estaAberto(LocalTime horario) {
        if (!ativo || tipo == TipoExcecao.FECHADO || horario == null) {
            return false;
        }
        
        if (tipo == TipoExcecao.HORARIO_ESPECIAL && horarioAbertura != null && horarioFechamento != null) {
            return !horario.isBefore(horarioAbertura) && horario.isBefore(horarioFechamento);
        }
        
        return false;
    }

    /**
     * Verifica se a exceção é válida para uma data específica.
     * 
     * @param data a data a ser verificada
     * @return true se a exceção se aplica à data
     */
    public boolean aplicavelNaData(LocalDate data) {
        return ativo && this.data.equals(data);
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
        validate();
    }

    public TipoExcecao getTipo() {
        return tipo;
    }

    public void setTipo(TipoExcecao tipo) {
        this.tipo = tipo;
        validate();
    }

    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
        validate();
    }

    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
        validate();
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
        FeriadoExcecao that = (FeriadoExcecao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FeriadoExcecao{" +
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

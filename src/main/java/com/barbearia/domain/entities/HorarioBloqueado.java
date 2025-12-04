package com.barbearia.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio para Horário Bloqueado.
 * 
 * Representa um slot de horário específico que está bloqueado e indisponível
 * para agendamentos.
 * Permite controle granular de disponibilidade dos profissionais.
 * 
 * Princípios POO aplicados:
 * - Encapsulamento: atributos privados com getters/setters
 * - Responsabilidade Única: gerencia apenas bloqueios de horários
 * - Validações de domínio: horário início antes do fim, campos obrigatórios
 * 
 * @author Sua Barbearia Team
 */
public class HorarioBloqueado {

    private Long id;
    private Long funcionarioId;
    private LocalDate data;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String motivo;
    private String criadoPor; // "BARBEARIA" ou "PROFISSIONAL"
    private LocalDateTime dataCriacao;

    public HorarioBloqueado() {
        this.dataCriacao = LocalDateTime.now();
    }

    public HorarioBloqueado(Long funcionarioId, LocalDate data,
            LocalTime horarioInicio, LocalTime horarioFim,
            String motivo, String criadoPor) {
        this.funcionarioId = funcionarioId;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.motivo = motivo;
        this.criadoPor = criadoPor;
        this.dataCriacao = LocalDateTime.now();
        validate();
    }

    /**
     * Valida as regras de negócio do bloqueio de horário.
     * 
     * @throws IllegalArgumentException se dados inválidos
     */
    public void validate() {
        if (funcionarioId == null) {
            throw new IllegalArgumentException("Funcionário é obrigatório");
        }

        if (data == null) {
            throw new IllegalArgumentException("Data é obrigatória");
        }

        if (horarioInicio == null) {
            throw new IllegalArgumentException("Horário de início é obrigatório");
        }

        if (horarioFim == null) {
            throw new IllegalArgumentException("Horário de fim é obrigatório");
        }

        if (criadoPor == null || criadoPor.isBlank()) {
            throw new IllegalArgumentException("Campo 'criadoPor' é obrigatório");
        }

        if (!criadoPor.equals("BARBEARIA") && !criadoPor.equals("PROFISSIONAL")) {
            throw new IllegalArgumentException("Campo 'criadoPor' deve ser 'BARBEARIA' ou 'PROFISSIONAL'");
        }

        if (!horarioInicio.isBefore(horarioFim)) {
            throw new IllegalArgumentException("Horário de início deve ser antes do horário de fim");
        }
    }

    /**
     * Verifica se este bloqueio é de hoje.
     */
    public boolean isHoje() {
        return this.data.equals(LocalDate.now());
    }

    /**
     * Verifica se foi criado pela barbearia.
     */
    public boolean isCriadoPorBarbearia() {
        return "BARBEARIA".equals(criadoPor);
    }

    /**
     * Verifica se foi criado pelo profissional.
     */
    public boolean isCriadoPorProfissional() {
        return "PROFISSIONAL".equals(criadoPor);
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HorarioBloqueado that = (HorarioBloqueado) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HorarioBloqueado{" +
                "id=" + id +
                ", funcionarioId=" + funcionarioId +
                ", data=" + data +
                ", horarioInicio=" + horarioInicio +
                ", horarioFim=" + horarioFim +
                ", motivo='" + motivo + '\'' +
                ", criadoPor='" + criadoPor + '\'' +
                '}';
    }
}

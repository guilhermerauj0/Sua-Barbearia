package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA para ProfissionalServico (many-to-many entre Funcionario e Servico).
 */
@Entity
@Table(name = "profissional_servicos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"funcionario_id", "servico_id"})
})
public class JpaProfissionalServico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;
    
    @Column(name = "servico_id", nullable = false)
    private Long servicoId;
    
    @Column(nullable = false)
    private boolean ativo;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    public JpaProfissionalServico() {
    }
    
    public JpaProfissionalServico(Long funcionarioId, Long servicoId) {
        this.funcionarioId = funcionarioId;
        this.servicoId = servicoId;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
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
    
    public Long getServicoId() {
        return servicoId;
    }
    
    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
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

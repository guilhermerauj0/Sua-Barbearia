package com.barbearia.domain.entities;

import java.time.LocalDateTime;

/**
 * Classe pivô que representa o relacionamento many-to-many entre Funcionario e Servico.
 * 
 * Permite vincular um funcionário a um serviço específico que ele é habilitado a executar.
 * Exemplo: FuncionarioBarbeiro está vinculado aos serviços CORTE e BARBA.
 * 
 * Conceitos POO:
 * - Encapsulamento: atributos privados com getters/setters
 * - Responsabilidade única: apenas gerencia o vínculo
 * 
 * @author Sua Barbearia Team
 */
public class ProfissionalServico {
    
    private Long id;
    private Long funcionarioId;
    private Long servicoId;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public ProfissionalServico() {
    }
    
    public ProfissionalServico(Long funcionarioId, Long servicoId) {
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

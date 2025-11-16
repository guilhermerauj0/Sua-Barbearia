package com.barbearia.domain.entities;

/**
 * Subclasse concreta de Funcionario para barbeiros.
 * Especialidade: Cortes de cabelo e barba.
 */
public class FuncionarioBarbeiro extends Funcionario {
    
    public FuncionarioBarbeiro() {
    }
    
    public FuncionarioBarbeiro(Long barbeariaId, String nome, String email, String telefone) {
        super(barbeariaId, nome, email, telefone);
    }
    
    public FuncionarioBarbeiro(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo) {
        super(id, nome, email, telefone, barbeariaId, ativo);
    }
    
    @Override
    public String getProfissao() {
        return "BARBEIRO";
    }
}

package com.barbearia.domain.entities;

/**
 * Subclasse concreta de Funcionario para esteticistas.
 * Especialidade: Design de sobrancelhas e tratamentos estéticos.
 */
public class FuncionarioEsteticista extends Funcionario {
    
    public FuncionarioEsteticista() {
    }
    
    public FuncionarioEsteticista(Long barbeariaId, String nome, String email, String telefone) {
        super(barbeariaId, nome, email, telefone);
    }
    
    public FuncionarioEsteticista(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo) {
        super(id, nome, email, telefone, barbeariaId, ativo);
    }
    
    @Override
    public String getProfissao() {
        return "ESTETICISTA";
    }
}

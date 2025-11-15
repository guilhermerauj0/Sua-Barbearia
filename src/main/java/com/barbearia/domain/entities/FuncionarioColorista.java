package com.barbearia.domain.entities;

/**
 * Subclasse concreta de Funcionario para coloristas.
 * Especialidade: Coloração capilar e tratamentos de cor.
 */
public class FuncionarioColorista extends Funcionario {
    
    public FuncionarioColorista() {
    }
    
    public FuncionarioColorista(Long barbeariaId, String nome, String email, String telefone) {
        super(barbeariaId, nome, email, telefone);
    }
    
    public FuncionarioColorista(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo) {
        super(id, nome, email, telefone, barbeariaId, ativo);
    }
    
    @Override
    public String getProfissao() {
        return "COLORISTA";
    }
}

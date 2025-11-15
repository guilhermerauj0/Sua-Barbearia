package com.barbearia.domain.entities;

/**
 * Subclasse concreta de Funcionario para manicures.
 * Especialidade: Manicure e cuidados com unhas.
 */
public class FuncionarioManicure extends Funcionario {
    
    public FuncionarioManicure() {
    }
    
    public FuncionarioManicure(Long barbeariaId, String nome, String email, String telefone) {
        super(barbeariaId, nome, email, telefone);
    }
    
    public FuncionarioManicure(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo) {
        super(id, nome, email, telefone, barbeariaId, ativo);
    }
    
    @Override
    public String getProfissao() {
        return "MANICURE";
    }
}

package com.barbearia.domain.entities;

import java.time.LocalDateTime;

/**
 * Classe abstrata que representa um funcionário da barbearia.
 * 
 * Cada funcionário tem uma profissão específica (barbeiro, manicure, esteticista, colorista).
 * Não herda de Usuario pois funcionários podem ter dados distintos de clientes/barbearias.
 * 
 * Conceitos POO:
 * - Abstração: classe abstrata com método polimórfico getProfissao()
 * - Encapsulamento: atributos privados com getters/setters
 * - Polimorfismo: cada subclasse implementa sua própria profissão
 * 
 * @author Sua Barbearia Team
 */
public abstract class Funcionario {
    
    private Long id;
    private Long barbeariaId;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public Funcionario() {
    }
    
    public Funcionario(Long barbeariaId, String nome, String email, String telefone) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Funcionario(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    /**
     * Retorna a profissão do funcionário.
     * Deve ser implementado por cada subclasse.
     * 
     * @return tipo de profissão (BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)
     */
    public abstract String getProfissao();
    
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
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

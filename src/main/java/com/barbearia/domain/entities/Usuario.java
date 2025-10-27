package com.barbearia.domain.entities;

import java.time.LocalDateTime;

/**
 * Classe abstrata que representa um usuário genérico do sistema.
 * 
 * Esta classe serve como base para diferentes tipos de usuários (Cliente, Barbeiro, etc).
 * Sendo abstrata, ela nunca será instanciada diretamente, apenas suas subclasses concretas.
 * 
 * Princípios aplicados:
 * - Abstração: Define o contrato comum para todos os usuários
 * - Encapsulamento: Atributos privados com getters/setters
 * - Herança: Permite que subclasses especializem o comportamento
 * 
 * @author Sua Barbearia Team
 */
public abstract class Usuario {
    
    /**
     * Identificador único do usuário no sistema
     */
    private Long id;
    
    /**
     * Nome completo do usuário
     */
    private String nome;
    
    /**
     * Email do usuário (deve ser único no sistema)
     */
    private String email;
    
    /**
     * Senha do usuário (deve ser armazenada com hash)
     */
    private String senha;
    
    /**
     * Telefone de contato do usuário
     */
    private String telefone;
    
    /**
     * Papel do usuário no sistema (CLIENTE, BARBEIRO, ADMIN)
     */
    private String role;
    
    /**
     * Data e hora de criação do registro
     */
    private LocalDateTime dataCriacao;
    
    /**
     * Data e hora da última atualização do registro
     */
    private LocalDateTime dataAtualizacao;
    
    /**
     * Construtor protegido para ser usado apenas pelas subclasses
     */
    protected Usuario() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    /**
     * Construtor com parâmetros essenciais
     * 
     * @param nome Nome completo do usuário
     * @param email Email válido e único
     * @param senha Senha que será armazenada com hash
     * @param telefone Telefone de contato
     * @param role Papel do usuário no sistema
     */
    protected Usuario(String nome, String email, String senha, String telefone, String role) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.role = role;
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
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
    
    /**
     * Atualiza a data de modificação para o momento atual.
     * Deve ser chamado sempre que o objeto for modificado.
     */
    public void atualizarDataModificacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}

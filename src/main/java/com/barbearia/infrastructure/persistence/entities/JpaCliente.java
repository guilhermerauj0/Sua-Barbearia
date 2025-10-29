package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa a tabela de Clientes no banco de dados.
 * 
 * Esta classe é específica para persistência e contém anotações JPA.
 * Ela NÃO é a mesma classe do domínio (Cliente) - isso é proposital!
 * 
 * Por que separar entidade de domínio da entidade JPA?
 * - Clean Architecture: domínio não deve depender de frameworks
 * - Flexibilidade: podemos mudar o banco sem afetar o domínio
 * - Testabilidade: domínio pode ser testado sem banco de dados
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "clientes")
public class JpaCliente {
    
    /**
     * Identificador único do cliente
     * Gerado automaticamente pelo banco (auto-incremento)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nome completo do cliente
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 100)
    private String nome;
    
    /**
     * Email do cliente
     * Deve ser único no banco (índice único)
     * Não pode ser nulo
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * Senha do cliente com hash
     * Nunca armazene senhas em texto puro!
     * Não pode ser nula
     */
    @Column(nullable = false, length = 255)
    private String senha;
    
    /**
     * Telefone do cliente (apenas números)
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 20)
    private String telefone;
    
    /**
     * Papel do usuário no sistema
     * Para clientes, sempre será "CLIENTE"
     */
    @Column(nullable = false, length = 20)
    private String role;
    
    /**
     * Indica se o cliente está ativo
     * Usado para "soft delete" (desativação ao invés de exclusão)
     */
    @Column(nullable = false)
    private boolean ativo = true;
    
    /**
     * Data e hora de criação do registro
     * Preenchida automaticamente na inserção
     */
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    /**
     * Data e hora da última atualização
     * Atualizada automaticamente nas modificações
     */
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
    
    /**
     * Método chamado automaticamente antes de persistir a entidade
     * Define as datas de criação e atualização
     */
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    /**
     * Método chamado automaticamente antes de atualizar a entidade
     * Atualiza a data de modificação
     */
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    // Construtores
    
    public JpaCliente() {
    }
    
    public JpaCliente(String nome, String email, String senha, String telefone, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.role = role;
        this.ativo = true;
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

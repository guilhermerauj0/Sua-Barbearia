package com.barbearia.infrastructure.persistence.entities;

import com.barbearia.domain.enums.TipoDocumento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa a tabela de Barbearias no banco de dados.
 * 
 * Esta classe é específica para persistência e contém anotações JPA.
 * Ela NÃO é a mesma classe do domínio (Barbearia) - isso é proposital!
 * 
 * Por que separar entidade de domínio da entidade JPA?
 * - Clean Architecture: domínio não deve depender de frameworks
 * - Flexibilidade: podemos mudar o banco sem afetar o domínio
 * - Testabilidade: domínio pode ser testado sem banco de dados
 * 
 * Constraints importantes:
 * - Email deve ser único
 * - Combinação de tipoDocumento + documento deve ser única
 *   (não pode ter dois CPFs iguais ou dois CNPJs iguais)
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "barbearias", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"tipo_documento", "documento"})
       })
public class JpaBarbearia {
    
    /**
     * Identificador único da barbearia
     * Gerado automaticamente pelo banco (auto-incremento)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nome completo ou razão social
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 100)
    private String nome;
    
    /**
     * Email da barbearia
     * Deve ser único no banco (índice único)
     * Não pode ser nulo
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * Senha com hash
     * Nunca armazene senhas em texto puro!
     * Não pode ser nula
     */
    @Column(nullable = false, length = 255)
    private String senha;
    
    /**
     * Telefone (apenas números)
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 20)
    private String telefone;
    
    /**
     * Nome fantasia do estabelecimento
     * Não pode ser nulo
     */
    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;
    
    /**
     * Tipo de documento (CPF ou CNPJ)
     * Armazenado como string no banco
     * Não pode ser nulo
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumento tipoDocumento;
    
    /**
     * Número do documento (apenas números)
     * Combinação de tipo_documento + documento é única
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 20)
    private String documento;
    
    /**
     * Endereço completo
     * Não pode ser nulo
     */
    @Column(nullable = false, length = 200)
    private String endereco;
    
    /**
     * Papel do usuário no sistema
     * Para barbearias, sempre será "BARBEARIA"
     */
    @Column(nullable = false, length = 20)
    private String role;
    
    /**
     * Indica se a barbearia está ativa
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
    
    public JpaBarbearia() {
    }
    
    public JpaBarbearia(String nome, String email, String senha, String telefone,
                        String nomeFantasia, TipoDocumento tipoDocumento,
                        String documento, String endereco, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.nomeFantasia = nomeFantasia;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.endereco = endereco;
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
    
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getDocumento() {
        return documento;
    }
    
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

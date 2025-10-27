package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para resposta de registro de cliente.
 * 
 * Este objeto é usado para retornar dados do cliente via API após o registro.
 * 
 * IMPORTANTE: Este DTO NÃO contém a senha por questões de segurança.
 * Nunca devemos expor senhas (mesmo com hash) nas respostas da API.
 * 
 * Por que usar um DTO diferente para resposta?
 * - Controle sobre quais dados são expostos (sem senha)
 * - Pode incluir dados calculados ou formatados
 * - Facilita mudanças na API sem afetar o domínio
 * 
 * @author Sua Barbearia Team
 */
public class ClienteResponseDto {
    
    /**
     * ID único do cliente no sistema
     */
    private Long id;
    
    /**
     * Nome completo do cliente
     */
    private String nome;
    
    /**
     * Email do cliente
     */
    private String email;
    
    /**
     * Telefone do cliente
     */
    private String telefone;
    
    /**
     * Papel do usuário (sempre será "CLIENTE")
     */
    private String role;
    
    /**
     * Indica se o cliente está ativo
     */
    private boolean ativo;
    
    /**
     * Data e hora em que o cliente foi registrado
     */
    private LocalDateTime dataCriacao;
    
    // Construtores
    
    public ClienteResponseDto() {
    }
    
    public ClienteResponseDto(Long id, String nome, String email, String telefone, String role, boolean ativo, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
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
    
    @Override
    public String toString() {
        return "ClienteResponseDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", role='" + role + '\'' +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}

package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoDocumento;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para resposta de barbearia.
 * 
 * Este objeto é retornado nas respostas HTTP após operações de registro ou consulta.
 * IMPORTANTE: Não inclui a senha por questões de segurança.
 * 
 * Campos retornados:
 * - id: Identificador único da barbearia
 * - nome: Razão social ou nome completo
 * - email: Email de contato
 * - telefone: Telefone sem formatação
 * - nomeFantasia: Nome fantasia do estabelecimento
 * - tipoDocumento: CPF ou CNPJ
 * - documento: Número do documento sem formatação
 * - endereco: Endereço completo
 * - role: Papel no sistema (sempre BARBEARIA)
 * - ativo: Status de ativação
 * - dataCriacao: Data e hora do registro
 * 
 * @author Sua Barbearia Team
 */
public class BarbeariaResponseDto {
    
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String nomeFantasia;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String endereco;
    private String role;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    
    /**
     * Construtor padrão
     */
    public BarbeariaResponseDto() {
    }
    
    /**
     * Construtor com todos os parâmetros
     */
    public BarbeariaResponseDto(Long id, String nome, String email, String telefone,
                                String nomeFantasia, TipoDocumento tipoDocumento,
                                String documento, String endereco, String role,
                                boolean ativo, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.nomeFantasia = nomeFantasia;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.endereco = endereco;
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
    
    @Override
    public String toString() {
        return "BarbeariaResponseDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", documento='" + documento + '\'' +
                ", endereco='" + endereco + '\'' +
                ", role='" + role + '\'' +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}

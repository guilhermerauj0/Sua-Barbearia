package com.barbearia.application.dto;

/**
 * DTO para listagem de barbearias.
 * 
 * Contém informações essenciais para exibição em listas e cards.
 * Não inclui informações sensíveis ou desnecessárias para listagem.
 * 
 * @author Sua Barbearia Team
 */
public class BarbeariaListItemDto {
    
    private Long id;
    private String nome;
    private String nomeFantasia;
    private String endereco;
    private String telefone;
    private String email;
    private Double avaliacaoMedia;
    private boolean ativo;
    
    public BarbeariaListItemDto() {
    }
    
    public BarbeariaListItemDto(Long id, String nome, String nomeFantasia, String endereco, 
                                String telefone, String email, Double avaliacaoMedia, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.avaliacaoMedia = avaliacaoMedia;
        this.ativo = ativo;
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
    
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Double getAvaliacaoMedia() {
        return avaliacaoMedia;
    }
    
    public void setAvaliacaoMedia(Double avaliacaoMedia) {
        this.avaliacaoMedia = avaliacaoMedia;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return "BarbeariaListItemDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", avaliacaoMedia=" + avaliacaoMedia +
                ", ativo=" + ativo +
                '}';
    }
}

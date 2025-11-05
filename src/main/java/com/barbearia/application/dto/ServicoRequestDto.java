package com.barbearia.application.dto;

import java.math.BigDecimal;

/**
 * DTO para requisição de criação de serviço.
 * 
 * Usado quando uma barbearia deseja cadastrar um novo serviço.
 * Contém validações básicas de negócio.
 * 
 * @author Sua Barbearia Team
 */
public class ServicoRequestDto {
    
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracao; // em minutos
    
    public ServicoRequestDto() {
    }
    
    public ServicoRequestDto(String nome, String descricao, BigDecimal preco, Integer duracao) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
    }
    
    // Getters e Setters
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getPreco() {
        return preco;
    }
    
    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
    
    public Integer getDuracao() {
        return duracao;
    }
    
    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }
    
    /**
     * Valida se todos os campos obrigatórios foram preenchidos.
     * 
     * @return true se válido, false caso contrário
     */
    public boolean isValid() {
        return nome != null && !nome.isBlank() &&
               preco != null && preco.compareTo(BigDecimal.ZERO) > 0 &&
               duracao != null && duracao > 0;
    }
}

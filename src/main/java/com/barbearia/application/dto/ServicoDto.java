package com.barbearia.application.dto;

import java.math.BigDecimal;

/**
 * DTO para exibição de serviços em listagens.
 * 
 * Contém informações do serviço necessárias para que clientes
 * possam escolher e agendar.
 * 
 * @author Sua Barbearia Team
 */
public class ServicoDto {
    
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracao; // em minutos
    private Long barbeariaId;
    private boolean ativo;
    private String tipoServico;
    
    public ServicoDto() {
    }
    
    public ServicoDto(Long id, String nome, String descricao, BigDecimal preco, 
                     Integer duracao, Long barbeariaId, boolean ativo, String tipoServico) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
        this.barbeariaId = barbeariaId;
        this.ativo = ativo;
        this.tipoServico = tipoServico;
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
    
    public Long getBarbeariaId() {
        return barbeariaId;
    }
    
    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    public String getTipoServico() {
        return tipoServico;
    }
    
    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }
    
    @Override
    public String toString() {
        return "ServicoDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", duracao=" + duracao +
                ", barbeariaId=" + barbeariaId +
                ", ativo=" + ativo +
                ", tipoServico='" + tipoServico + '\'' +
                '}';
    }
}

package com.barbearia.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um Serviço oferecido.
 * 
 * Um serviço é um procedimento disponível em uma barbearia,
 * como corte de cabelo, barba, etc.
 * 
 * Conceitos de POO aplicados:
 * - Abstração: Define a interface para todos os serviços
 * - Encapsulamento: Atributos privados com getters/setters
 * - Polimorfismo: Cada subclasse pode ter comportamento específico
 * 
 * @author Sua Barbearia Team
 */
public abstract class Servico {
    
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracao; // em minutos
    private Long barbeariaId;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public Servico() {
    }
    
    public Servico(Long id, String nome, String descricao, BigDecimal preco, 
                   Integer duracao, Long barbeariaId, boolean ativo, 
                   LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
        this.barbeariaId = barbeariaId;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
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
     * Método abstrato que define o tipo de serviço.
     * Cada subclasse implementará com seu tipo específico.
     * 
     * @return O tipo de serviço (CORTE, BARBA, MANICURE, etc.)
     */
    public abstract String getTipoServico();
    
    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", duracao=" + duracao +
                ", barbeariaId=" + barbeariaId +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}

package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela 'servicos' do banco de dados.
 * 
 * Representa um serviço oferecido por uma barbearia.
 * Usa herança do tipo JOINED para separar dados comuns em uma tabela base
 * e dados específicos em tabelas filhas.
 * 
 * Herança: Estratégia JOINED - a tabela base contém os atributos comuns
 * e cada subclasse tem sua própria tabela.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_servico", discriminatorType = DiscriminatorType.STRING)
public abstract class JpaServico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    
    @Column(nullable = false)
    private Integer duracao; // em minutos
    
    @Column(nullable = false, name = "barbearia_id")
    private Long barbeariaId;
    
    @Column(nullable = false)
    private boolean ativo = true;
    
    @Column(nullable = false, name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false, name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "tipo_servico", insertable = false, updatable = false)
    private String tipoServico;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public JpaServico() {
    }
    
    public JpaServico(String nome, String descricao, BigDecimal preco, 
                     Integer duracao, Long barbeariaId) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
        this.barbeariaId = barbeariaId;
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
    
    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses.
     * Retorna o tipo específico de serviço.
     * 
     * @return O tipo de serviço (CORTE, BARBA, MANICURE, etc.)
     */
    public abstract String getTipoServico();
    
    @Override
    public String toString() {
        return "JpaServico{" +
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

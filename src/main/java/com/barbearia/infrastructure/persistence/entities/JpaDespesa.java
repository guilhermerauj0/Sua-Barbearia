package com.barbearia.infrastructure.persistence.entities;

import com.barbearia.domain.enums.CategoriaDespesa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma Despesa no banco de dados.
 * 
 * <p>
 * Despesas são saídas financeiras da barbearia, como aluguel, contas, salários,
 * etc.
 * </p>
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "despesas", indexes = {
        @Index(name = "idx_despesa_barbearia_data", columnList = "barbearia_id, data_transacao")
})
public class JpaDespesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID da barbearia proprietária desta despesa.
     */
    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;

    /**
     * Valor monetário da despesa.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /**
     * Categoria da despesa para fins de relatório.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaDespesa categoria;

    /**
     * Descrição detalhada da despesa.
     */
    @Column(length = 500)
    private String descricao;

    /**
     * Data em que a transação ocorreu (competência).
     */
    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    /**
     * Data de criação do registro no sistema.
     */
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data da última atualização do registro.
     */
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Constructors
    public JpaDespesa() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public CategoriaDespesa getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDespesa categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
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

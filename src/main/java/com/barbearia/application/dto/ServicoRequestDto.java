package com.barbearia.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO para requisição de criação de serviço.
 * 
 * Usado quando uma barbearia deseja cadastrar um novo serviço.
 * Contém validações básicas de negócio.
 * 
 * Todos os campos são obrigatórios, incluindo o tipoServico.
 * Valores permitidos para tipoServico: CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO, TRATAMENTO_CAPILAR
 * 
 * @author Sua Barbearia Team
 */
public class ServicoRequestDto {
    
    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;
    
    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser maior que zero (em minutos)")
    private Integer duracao; // em minutos
    
    @NotBlank(message = "Tipo de serviço é obrigatório")
    private String tipoServico; // CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO, TRATAMENTO_CAPILAR
    
    public ServicoRequestDto() {
    }
    
    public ServicoRequestDto(String nome, String descricao, BigDecimal preco, Integer duracao) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
    }

    public ServicoRequestDto(String nome, String descricao, BigDecimal preco, Integer duracao, String tipoServico) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracao = duracao;
        this.tipoServico = tipoServico;
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

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }
    
    /**
     * Valida se todos os campos obrigatórios foram preenchidos.
     * 
     * @return true se válido, false caso contrário
     */
    public boolean isValid() {
        return nome != null && !nome.isBlank() &&
               preco != null && preco.compareTo(BigDecimal.ZERO) > 0 &&
               duracao != null && duracao > 0 &&
               tipoServico != null && !tipoServico.isBlank() &&
               isValidTipoServico(tipoServico);
    }

    /**
     * Valida se o tipo de serviço é um dos valores permitidos.
     * 
     * @param tipo tipo de serviço a validar
     * @return true se válido, false caso contrário
     */
    private boolean isValidTipoServico(String tipo) {
        return tipo.equals("CORTE") || 
               tipo.equals("BARBA") || 
               tipo.equals("MANICURE") || 
               tipo.equals("SOBRANCELHA") || 
               tipo.equals("COLORACAO") || 
               tipo.equals("TRATAMENTO_CAPILAR");
    }
}

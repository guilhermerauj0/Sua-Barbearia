package com.barbearia.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Subclasse concreta de Servico que representa um Serviço de Tratamento Capilar.
 * 
 * Herança: Estende a classe abstrata Servico
 * Polimorfismo: Implementa getTipoServico() com comportamento específico
 * 
 * @author Sua Barbearia Team
 */
public class ServicoTratamentoCapilar extends Servico {
    
    public ServicoTratamentoCapilar() {
        super();
    }
    
    public ServicoTratamentoCapilar(Long id, String nome, String descricao, BigDecimal preco, 
                                    Integer duracao, Long barbeariaId, boolean ativo, 
                                    LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        super(id, nome, descricao, preco, duracao, barbeariaId, ativo, dataCriacao, dataAtualizacao);
    }
    
    @Override
    public String getTipoServico() {
        return "TRATAMENTO_CAPILAR";
    }
}

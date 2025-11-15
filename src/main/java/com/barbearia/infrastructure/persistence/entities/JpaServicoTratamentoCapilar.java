package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Subclasse JPA de Serviço para Tratamento Capilar.
 * Usa herança JOINED - tem sua própria tabela para dados específicos.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos_tratamento_capilar")
@DiscriminatorValue("TRATAMENTO_CAPILAR")
public class JpaServicoTratamentoCapilar extends JpaServico {
    
    @Override
    public String getTipoServico() {
        return "TRATAMENTO_CAPILAR";
    }
}

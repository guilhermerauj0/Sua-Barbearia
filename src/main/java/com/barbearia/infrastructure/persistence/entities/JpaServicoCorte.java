package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Subclasse JPA de Serviço para Corte.
 * Usa herança JOINED - tem sua própria tabela para dados específicos.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos_corte")
@DiscriminatorValue("CORTE")
public class JpaServicoCorte extends JpaServico {
    
    @Override
    public String getTipoServico() {
        return "CORTE";
    }
}

package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Subclasse JPA de Serviço para Barba.
 * Usa herança JOINED - tem sua própria tabela para dados específicos.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos_barba")
@DiscriminatorValue("BARBA")
public class JpaServicoBarba extends JpaServico {
    
    @Override
    public String getTipoServico() {
        return "BARBA";
    }
}

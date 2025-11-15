package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Subclasse JPA de Serviço para Manicure.
 * Usa herança JOINED - tem sua própria tabela para dados específicos.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos_manicure")
@DiscriminatorValue("MANICURE")
public class JpaServicoManicure extends JpaServico {
    
    @Override
    public String getTipoServico() {
        return "MANICURE";
    }
}

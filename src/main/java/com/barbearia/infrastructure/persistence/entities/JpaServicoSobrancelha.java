package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Subclasse JPA de Serviço para Sobrancelha.
 * Usa herança JOINED - tem sua própria tabela para dados específicos.
 * 
 * @author Sua Barbearia Team
 */
@Entity
@Table(name = "servicos_sobrancelha")
@DiscriminatorValue("SOBRANCELHA")
public class JpaServicoSobrancelha extends JpaServico {
    
    @Override
    public String getTipoServico() {
        return "SOBRANCELHA";
    }
}

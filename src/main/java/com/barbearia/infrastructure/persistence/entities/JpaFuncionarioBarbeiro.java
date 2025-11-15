package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA para Barbeiro com discriminator value BARBEIRO.
 */
@Entity
@Table(name = "funcionarios_barbeiro")
@DiscriminatorValue("BARBEIRO")
public class JpaFuncionarioBarbeiro extends JpaFuncionario {
    
    @Override
    public String getProfissao() {
        return "BARBEIRO";
    }
}

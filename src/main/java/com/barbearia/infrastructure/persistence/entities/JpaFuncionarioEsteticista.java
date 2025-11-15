package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA para Esteticista com discriminator value ESTETICISTA.
 */
@Entity
@Table(name = "funcionarios_esteticista")
@DiscriminatorValue("ESTETICISTA")
public class JpaFuncionarioEsteticista extends JpaFuncionario {
    
    @Override
    public String getProfissao() {
        return "ESTETICISTA";
    }
}

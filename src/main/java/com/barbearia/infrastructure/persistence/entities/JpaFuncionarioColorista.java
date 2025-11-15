package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA para Colorista com discriminator value COLORISTA.
 */
@Entity
@Table(name = "funcionarios_colorista")
@DiscriminatorValue("COLORISTA")
public class JpaFuncionarioColorista extends JpaFuncionario {
    
    @Override
    public String getProfissao() {
        return "COLORISTA";
    }
}

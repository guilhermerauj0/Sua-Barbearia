package com.barbearia.infrastructure.persistence.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA para Manicure com discriminator value MANICURE.
 */
@Entity
@Table(name = "funcionarios_manicure")
@DiscriminatorValue("MANICURE")
public class JpaFuncionarioManicure extends JpaFuncionario {
    
    @Override
    public String getProfissao() {
        return "MANICURE";
    }
}

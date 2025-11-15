package com.barbearia.adapters.mappers;

import com.barbearia.domain.entities.ProfissionalServico;
import com.barbearia.infrastructure.persistence.entities.JpaProfissionalServico;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre diferentes representações de ProfissionalServico.
 * 
 * Converte entre:
 * - Domínio (ProfissionalServico) <-> JPA (JpaProfissionalServico)
 * 
 * @author Sua Barbearia Team
 */
@Component
public class ProfissionalServicoMapper {
    
    /**
     * Converte JpaProfissionalServico para ProfissionalServico de domínio.
     */
    public static ProfissionalServico toDomain(JpaProfissionalServico jpaProfissionalServico) {
        if (jpaProfissionalServico == null) {
            return null;
        }
        
        ProfissionalServico profissionalServico = new ProfissionalServico(
            jpaProfissionalServico.getFuncionarioId(),
            jpaProfissionalServico.getServicoId()
        );
        profissionalServico.setId(jpaProfissionalServico.getId());
        profissionalServico.setAtivo(jpaProfissionalServico.isAtivo());
        profissionalServico.setDataCriacao(jpaProfissionalServico.getDataCriacao());
        profissionalServico.setDataAtualizacao(jpaProfissionalServico.getDataAtualizacao());
        
        return profissionalServico;
    }
    
    /**
     * Converte ProfissionalServico de domínio para JpaProfissionalServico.
     */
    public static JpaProfissionalServico toJpaEntity(ProfissionalServico profissionalServico) {
        if (profissionalServico == null) {
            return null;
        }
        
        JpaProfissionalServico jpaProfissionalServico = new JpaProfissionalServico(
            profissionalServico.getFuncionarioId(),
            profissionalServico.getServicoId()
        );
        jpaProfissionalServico.setId(profissionalServico.getId());
        jpaProfissionalServico.setAtivo(profissionalServico.isAtivo());
        jpaProfissionalServico.setDataCriacao(profissionalServico.getDataCriacao());
        jpaProfissionalServico.setDataAtualizacao(profissionalServico.getDataAtualizacao());
        
        return jpaProfissionalServico;
    }
}

package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.domain.entities.HorarioFuncionamento;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento;

/**
 * Mapper para converter entre HorarioFuncionamento (domain) e JpaHorarioFuncionamento (persistence).
 * Também fornece conversões para/de DTOs.
 */
public class HorarioFuncionamentoMapper {
    
    /**
     * Converte uma entidade JPA para DTO de resposta
     * 
     * @param jpaEntity Entidade JPA
     * @return DTO de resposta
     */
    public static HorarioFuncionamentoResponseDto toResponseDto(JpaHorarioFuncionamento jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return new HorarioFuncionamentoResponseDto(
            jpaEntity.getId(),
            jpaEntity.getBarbeariaId(),
            jpaEntity.getDiaSemana(),
            jpaEntity.getHoraAbertura(),
            jpaEntity.getHoraFechamento(),
            jpaEntity.isAtivo()
        );
    }
    
    /**
     * Converte uma entidade JPA para domain
     * 
     * @param jpaEntity Entidade JPA
     * @return Entidade domain
     */
    public static HorarioFuncionamento toDomain(JpaHorarioFuncionamento jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return new HorarioFuncionamento(
            jpaEntity.getBarbeariaId(),
            jpaEntity.getDiaSemana(),
            jpaEntity.getHoraAbertura(),
            jpaEntity.getHoraFechamento()
        );
    }
    
    /**
     * Converte uma entidade domain para JPA
     * 
     * @param domain Entidade domain
     * @return Entidade JPA
     */
    public static JpaHorarioFuncionamento toJpaEntity(HorarioFuncionamento domain) {
        if (domain == null) {
            return null;
        }
        
        JpaHorarioFuncionamento jpa = new JpaHorarioFuncionamento(
            domain.getBarbeariaId(),
            domain.getDiaSemana(),
            domain.getHoraAbertura(),
            domain.getHoraFechamento()
        );
        jpa.setAtivo(true);
        
        return jpa;
    }
}

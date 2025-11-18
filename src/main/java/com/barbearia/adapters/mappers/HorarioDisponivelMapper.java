package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.domain.entities.Funcionario;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Mapper para conversão para HorarioDisponivelDto (disponibilidade de agendamento).
 * 
 * Converte dados de funcionário e horários em DTO com informações de disponibilidade.
 * 
 * @author Sua Barbearia Team
 */
@Component
public class HorarioDisponivelMapper {
    
    /**
     * Converte JpaFuncionario e horários em HorarioDisponivelDto.
     */
    public static HorarioDisponivelDto toDto(JpaFuncionario jpaFuncionario, LocalDate data, 
                                            LocalTime horarioInicio, LocalTime horarioFim) {
        if (jpaFuncionario == null) {
            return null;
        }
        
        return new HorarioDisponivelDto(
            jpaFuncionario.getId(),
            jpaFuncionario.getNome(),
            jpaFuncionario.getPerfilType().name(),
            data,
            horarioInicio,
            horarioFim
        );
    }
    
    /**
     * Converte Funcionario de domínio e horários em HorarioDisponivelDto.
     */
    public static HorarioDisponivelDto toDto(Funcionario funcionario, LocalDate data, 
                                            LocalTime horarioInicio, LocalTime horarioFim) {
        if (funcionario == null) {
            return null;
        }
        
        return new HorarioDisponivelDto(
            funcionario.getId(),
            funcionario.getNome(),
            funcionario.getProfissao(),
            data,
            horarioInicio,
            horarioFim
        );
    }
}

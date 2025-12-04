package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaHorarioBloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para gerenciar Horários Bloqueados.
 * 
 * Fornece queries para buscar bloqueios por período e funcionário.
 */
@Repository
public interface HorarioBloqueadoRepository extends JpaRepository<JpaHorarioBloqueado, Long> {

    /**
     * Lista todos os bloqueios de um funcionário em uma data específica.
     */
    List<JpaHorarioBloqueado> findByFuncionarioIdAndData(Long funcionarioId, LocalDate data);

    /**
     * Lista bloqueios de um funcionário em um período.
     */
    @Query("SELECT h FROM JpaHorarioBloqueado h WHERE h.funcionarioId = :funcionarioId " +
            "AND h.data BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY h.data, h.horarioInicio")
    List<JpaHorarioBloqueado> findByFuncionarioIdAndPeriodo(
            @Param("funcionarioId") Long funcionarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    /**
     * Lista bloqueios de um funcionário criados por origem específica.
     */
    List<JpaHorarioBloqueado> findByFuncionarioIdAndCriadoPor(Long funcionarioId, String criadoPor);

    /**
     * Remove todos os bloqueios de um funcionário em uma data específica.
     */
    void deleteByFuncionarioIdAndData(Long funcionarioId, LocalDate data);

    /**
     * Verifica se existe sobreposição de horário bloqueado.
     */
    @Query("SELECT COUNT(h) > 0 FROM JpaHorarioBloqueado h " +
            "WHERE h.funcionarioId = :funcionarioId " +
            "AND h.data = :data " +
            "AND ((h.horarioInicio < :horarioFim AND h.horarioFim > :horarioInicio))")
    boolean existsSobreposicao(
            @Param("funcionarioId") Long funcionarioId,
            @Param("data") LocalDate data,
            @Param("horarioInicio") java.time.LocalTime horarioInicio,
            @Param("horarioFim") java.time.LocalTime horarioFim);
}

package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para HorarioFuncionamento (horário de funcionamento por dia da semana).
 */
@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<JpaHorarioFuncionamento, Long> {
    
    /**
     * Encontra todos os horários de uma barbearia (apenas ativos).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.barbeariaId = :barbeariaId AND h.ativo = true ORDER BY h.diaSemana ASC")
    List<JpaHorarioFuncionamento> findByBarbeariaIdAtivo(@Param("barbeariaId") Long barbeariaId);
    
    /**
     * Encontra todos os horários de uma barbearia (ativos ou não).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.barbeariaId = :barbeariaId ORDER BY h.diaSemana ASC")
    List<JpaHorarioFuncionamento> findByBarbeariaId(@Param("barbeariaId") Long barbeariaId);
    
    /**
     * Encontra o horário de funcionamento de uma barbearia para um dia específico (apenas ativo).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.barbeariaId = :barbeariaId AND h.diaSemana = :diaSemana AND h.ativo = true")
    Optional<JpaHorarioFuncionamento> findByBarbeariaIdAndDiaSemanaAtivo(@Param("barbeariaId") Long barbeariaId, @Param("diaSemana") Integer diaSemana);
    
    /**
     * Encontra o horário de funcionamento de uma barbearia para um dia específico (ignora status ativo).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.barbeariaId = :barbeariaId AND h.diaSemana = :diaSemana")
    Optional<JpaHorarioFuncionamento> findByBarbeariaIdAndDiaSemana(@Param("barbeariaId") Long barbeariaId, @Param("diaSemana") Integer diaSemana);
    
    /**
     * Verifica se uma barbearia possui horários configurados para um dia específico.
     */
    @Query("SELECT COUNT(h) > 0 FROM JpaHorarioFuncionamento h WHERE h.barbeariaId = :barbeariaId AND h.diaSemana = :diaSemana AND h.ativo = true")
    boolean existsForBarbeariaAndDiaAtivo(@Param("barbeariaId") Long barbeariaId, @Param("diaSemana") Integer diaSemana);

    /**
     * Encontra o horário de funcionamento de um funcionário para um dia específico (apenas ativo).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.funcionarioId = :funcionarioId AND h.diaSemana = :diaSemana AND h.ativo = true")
    Optional<JpaHorarioFuncionamento> findByFuncionarioIdAndDiaSemanaAtivo(@Param("funcionarioId") Long funcionarioId, @Param("diaSemana") Integer diaSemana);

    /**
     * Encontra todos os horários de um funcionário (apenas ativos).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.funcionarioId = :funcionarioId AND h.ativo = true ORDER BY h.diaSemana ASC")
    List<JpaHorarioFuncionamento> findByFuncionarioIdAtivo(@Param("funcionarioId") Long funcionarioId);

    /**
     * Encontra o horário de funcionamento de um funcionário para um dia específico (ignora status ativo).
     */
    @Query("SELECT h FROM JpaHorarioFuncionamento h WHERE h.funcionarioId = :funcionarioId AND h.diaSemana = :diaSemana")
    Optional<JpaHorarioFuncionamento> findByFuncionarioIdAndDiaSemana(@Param("funcionarioId") Long funcionarioId, @Param("diaSemana") Integer diaSemana);
}

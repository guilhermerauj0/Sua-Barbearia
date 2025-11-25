package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioExcecaoRepository extends JpaRepository<JpaHorarioExcecao, Long> {

        @Query("SELECT e FROM JpaHorarioExcecao e WHERE e.funcionarioId = :funcionarioId AND e.data = :data AND e.ativo = true")
        Optional<JpaHorarioExcecao> findByFuncionarioIdAndData(
                        @Param("funcionarioId") Long funcionarioId,
                        @Param("data") LocalDate data);

        @Query("SELECT e FROM JpaHorarioExcecao e WHERE e.funcionarioId = :funcionarioId " +
                        "AND e.data BETWEEN :dataInicio AND :dataFim AND e.ativo = true ORDER BY e.data")
        List<JpaHorarioExcecao> findByFuncionarioIdAndPeriodo(
                        @Param("funcionarioId") Long funcionarioId,
                        @Param("dataInicio") LocalDate dataInicio,
                        @Param("dataFim") LocalDate dataFim);

        @Query("SELECT e FROM JpaHorarioExcecao e WHERE e.funcionarioId = :funcionarioId AND e.ativo = true ORDER BY e.data")
        List<JpaHorarioExcecao> findByFuncionarioIdAndAtivoTrue(@Param("funcionarioId") Long funcionarioId);

        @Query("SELECT COUNT(e) > 0 FROM JpaHorarioExcecao e WHERE e.funcionarioId = :funcionarioId AND e.data = :data AND e.ativo = true")
        boolean existsByFuncionarioIdAndData(
                        @Param("funcionarioId") Long funcionarioId,
                        @Param("data") LocalDate data);
}

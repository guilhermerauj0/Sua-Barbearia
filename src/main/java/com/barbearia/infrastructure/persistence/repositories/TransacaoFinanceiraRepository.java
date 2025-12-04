package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.domain.enums.TipoTransacao;
import com.barbearia.infrastructure.persistence.entities.JpaTransacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para transações financeiras (despesas e receitas extras).
 */
@Repository
public interface TransacaoFinanceiraRepository extends JpaRepository<JpaTransacaoFinanceira, Long> {

    /**
     * Busca transações por barbearia, tipo e período.
     */
    @Query("SELECT t FROM JpaTransacaoFinanceira t " +
            "WHERE t.barbeariaId = :barbeariaId " +
            "AND t.tipoTransacao = :tipo " +
            "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY t.dataTransacao DESC")
    List<JpaTransacaoFinanceira> findByPeriodoETipo(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoTransacao tipo);

    /**
     * Busca transações por barbearia, tipo, período e categoria.
     */
    @Query("SELECT t FROM JpaTransacaoFinanceira t " +
            "WHERE t.barbeariaId = :barbeariaId " +
            "AND t.tipoTransacao = :tipo " +
            "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim " +
            "AND t.categoria = :categoria " +
            "ORDER BY t.dataTransacao DESC")
    List<JpaTransacaoFinanceira> findByPeriodoTipoECategoria(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoTransacao tipo,
            @Param("categoria") String categoria);

    /**
     * Calcula total por período e tipo.
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM JpaTransacaoFinanceira t " +
            "WHERE t.barbeariaId = :barbeariaId " +
            "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim " +
            "AND t.tipoTransacao = :tipo")
    BigDecimal calcularTotalPorPeriodo(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoTransacao tipo);

    /**
     * Obtém resumo por categoria (categoria, total, quantidade).
     */
    @Query("SELECT t.categoria, SUM(t.valor), COUNT(t) " +
            "FROM JpaTransacaoFinanceira t " +
            "WHERE t.barbeariaId = :barbeariaId " +
            "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim " +
            "AND t.tipoTransacao = :tipo " +
            "GROUP BY t.categoria " +
            "ORDER BY SUM(t.valor) DESC")
    List<Object[]> obterResumoPorCategoria(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoTransacao tipo);

    /**
     * Verifica se a transação pertence à barbearia.
     */
    boolean existsByIdAndBarbeariaId(Long id, Long barbeariaId);
}

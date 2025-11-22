package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar Avaliações.
 * 
 * Fornece queries personalizadas para cálculo de médias e estatísticas.
 */
@Repository
public interface AvaliacaoRepository extends JpaRepository<JpaAvaliacao, Long> {

    /**
     * Lista todas as avaliações de uma barbearia, ordenadas por data (mais recentes
     * primeiro).
     */
    List<JpaAvaliacao> findByBarbeariaIdOrderByDataCriacaoDesc(Long barbeariaId);

    /**
     * Busca avaliação específica de um cliente para um agendamento.
     * Garante constraint: uma avaliação por agendamento.
     */
    Optional<JpaAvaliacao> findByClienteIdAndAgendamentoId(Long clienteId, Long agendamentoId);

    /**
     * Verifica se já existe avaliação para um agendamento.
     */
    boolean existsByAgendamentoId(Long agendamentoId);

    /**
     * Calcula média geral das avaliações de uma barbearia.
     */
    @Query("SELECT AVG(a.notaGeral) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId")
    Double calcularMediaGeral(@Param("barbeariaId") Long barbeariaId);

    /**
     * Calcula média da nota de serviço.
     */
    @Query("SELECT AVG(a.notaServico) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId")
    Double calcularMediaServico(@Param("barbeariaId") Long barbeariaId);

    /**
     * Calcula média da nota de ambiente.
     */
    @Query("SELECT AVG(a.notaAmbiente) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId")
    Double calcularMediaAmbiente(@Param("barbeariaId") Long barbeariaId);

    /**
     * Calcula média da nota de limpeza.
     */
    @Query("SELECT AVG(a.notaLimpeza) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId")
    Double calcularMediaLimpeza(@Param("barbeariaId") Long barbeariaId);

    /**
     * Calcula média da nota de atendimento.
     */
    @Query("SELECT AVG(a.notaAtendimento) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId")
    Double calcularMediaAtendimento(@Param("barbeariaId") Long barbeariaId);

    /**
     * Conta total de avaliações de uma barbearia.
     */
    long countByBarbeariaId(Long barbeariaId);

    /**
     * Conta avaliações por nota geral (para distribuição).
     */
    @Query("SELECT COUNT(a) FROM JpaAvaliacao a WHERE a.barbeariaId = :barbeariaId " +
            "AND FLOOR(a.notaGeral) = :nota")
    long countByBarbeariaIdAndNotaGeral(@Param("barbeariaId") Long barbeariaId,
            @Param("nota") int nota);
}

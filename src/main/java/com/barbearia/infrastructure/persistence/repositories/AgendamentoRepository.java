package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para acesso aos dados de Agendamento no banco de dados.
 * 
 * Queries personalizadas para:
 * - Buscar agendamentos passados de um cliente
 * - Buscar agendamentos futuros de um cliente
 * - Buscar agendamentos de uma barbearia em um período
 * 
 * @author Sua Barbearia Team
 */
@Repository
public interface AgendamentoRepository extends JpaRepository<JpaAgendamento, Long> {
    
    /**
     * Busca agendamentos passados de um cliente específico.
     * 
     * Retorna apenas agendamentos com dataHora < agora
     * Ordenados por data decrescente (mais recente primeiro)
     * 
     * @param clienteId ID do cliente
     * @param dataHoraAtual Data e hora atual para comparação
     * @return Lista de agendamentos passados
     */
    @Query("SELECT a FROM JpaAgendamento a " +
           "WHERE a.clienteId = :clienteId " +
           "AND a.dataHora < :dataHoraAtual " +
           "ORDER BY a.dataHora DESC")
    List<JpaAgendamento> findHistoricoByClienteId(
            @Param("clienteId") Long clienteId,
            @Param("dataHoraAtual") LocalDateTime dataHoraAtual
    );
    
    /**
     * Busca agendamentos futuros de um cliente específico.
     * 
     * @param clienteId ID do cliente
     * @param dataHoraAtual Data e hora atual para comparação
     * @return Lista de agendamentos futuros
     */
    @Query("SELECT a FROM JpaAgendamento a " +
           "WHERE a.clienteId = :clienteId " +
           "AND a.dataHora >= :dataHoraAtual " +
           "ORDER BY a.dataHora ASC")
    List<JpaAgendamento> findAgendamentosFuturosByClienteId(
            @Param("clienteId") Long clienteId,
            @Param("dataHoraAtual") LocalDateTime dataHoraAtual
    );
    
    /**
     * Busca todos os agendamentos de um cliente (histórico + futuros)
     * 
     * @param clienteId ID do cliente
     * @return Lista de todos os agendamentos do cliente
     */
    List<JpaAgendamento> findByClienteIdOrderByDataHoraDesc(Long clienteId);
    
    /**
     * Busca agendamentos de uma barbearia em um período
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de agendamentos no período
     */
    @Query("SELECT a FROM JpaAgendamento a " +
           "WHERE a.barbeariaId = :barbeariaId " +
           "AND a.dataHora BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY a.dataHora ASC")
    List<JpaAgendamento> findByBarbeariaIdAndPeriodo(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
    
    /**
     * Busca agendamentos de um funcionário (barbeiro) em um dia específico
     * 
     * @param barbeiroId ID do barbeiro/funcionário
     * @param dataInicio Início do dia (00:00:00)
     * @param dataFim Fim do dia (23:59:59)
     * @return Lista de agendamentos do funcionário naquele dia
     */
    @Query("SELECT a FROM JpaAgendamento a " +
           "WHERE a.barbeiroId = :barbeiroId " +
           "AND a.dataHora >= :dataInicio " +
           "AND a.dataHora < :dataFim " +
           "ORDER BY a.dataHora ASC")
    List<JpaAgendamento> findByBarbeiroIdAndDataBetween(
            @Param("barbeiroId") Long barbeiroId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}

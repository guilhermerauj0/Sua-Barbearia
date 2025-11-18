package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    
    /**
     * Verifica se há conflito de horário para um funcionário.
     * 
     * Busca agendamentos do funcionário na mesma data/hora.
     * 
     * @param barbeiroId ID do barbeiro/funcionário
     * @param dataHora Data e hora do agendamento
     * @return true se há conflito, false caso contrário
     */
    @Query("SELECT COUNT(a) > 0 FROM JpaAgendamento a " +
           "WHERE a.barbeiroId = :barbeiroId " +
           "AND a.dataHora = :dataHora " +
           "AND a.status != 'CANCELADO'")
    boolean existsConflictByBarbeiroIdAndDataHora(
            @Param("barbeiroId") Long barbeiroId,
            @Param("dataHora") LocalDateTime dataHora
    );
    
    /**
     * Busca agendamentos de uma barbearia em um intervalo de datas.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data/hora inicial do intervalo
     * @param dataFim Data/hora final do intervalo
     * @return Lista de agendamentos ordenados por data/hora ascendente
     */
    List<JpaAgendamento> findByBarbeariaIdAndDataHoraBetweenOrderByDataHoraAsc(
            Long barbeariaId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );
    
    /**
     * Busca todos os agendamentos de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de agendamentos ordenados por data/hora decrescente
     */
    List<JpaAgendamento> findByBarbeariaIdOrderByDataHoraDesc(Long barbeariaId);
    
    /**
     * Calcula o faturamento total da barbearia em um período.
     * 
     * Query nativa SQL para melhor performance.
     * Considera apenas agendamentos com status CONCLUIDO.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data/hora inicial do período
     * @param dataFim Data/hora final do período
     * @return Faturamento total do período (soma dos preços dos serviços)
     */
    @Query(value = """
            SELECT COALESCE(SUM(s.preco), 0) 
            FROM agendamentos a 
            INNER JOIN servicos s ON a.servico_id = s.id 
            WHERE a.barbearia_id = :barbeariaId 
            AND a.data_hora BETWEEN :dataInicio AND :dataFim 
            AND a.status = 'CONCLUIDO'
            """, nativeQuery = true)
    BigDecimal calcularFaturamentoPorPeriodo(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
    
    /**
     * Conta o total de agendamentos concluídos em um período.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data/hora inicial do período
     * @param dataFim Data/hora final do período
     * @return Quantidade de agendamentos concluídos
     */
    @Query("SELECT COUNT(a) FROM JpaAgendamento a " +
           "WHERE a.barbeariaId = :barbeariaId " +
           "AND a.dataHora BETWEEN :dataInicio AND :dataFim " +
           "AND a.status = 'CONCLUIDO'")
    Long contarAgendamentosConcluidosPorPeriodo(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
    
    /**
     * Retorna os serviços mais rentáveis da barbearia em um período.
     * 
     * Query nativa SQL com agregação e ordenação por faturamento.
     * Retorna: servicoId, servicoNome, totalRealizacoes, faturamentoTotal
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data/hora inicial do período
     * @param dataFim Data/hora final do período
     * @return Lista de arrays com [servicoId, servicoNome, totalRealizacoes, faturamentoTotal]
     */
    @Query(value = """
            SELECT 
                s.id as servicoId,
                s.nome as servicoNome,
                COUNT(a.id) as totalRealizacoes,
                SUM(s.preco) as faturamentoTotal
            FROM agendamentos a
            INNER JOIN servicos s ON a.servico_id = s.id
            WHERE a.barbearia_id = :barbeariaId
            AND a.data_hora BETWEEN :dataInicio AND :dataFim
            AND a.status = 'CONCLUIDO'
            GROUP BY s.id, s.nome
            ORDER BY faturamentoTotal DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> buscarServicosMaisRentaveis(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
    
    /**
     * Busca agendamentos de uma barbearia em um período com status específico.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data/hora de início
     * @param dataFim Data/hora de fim
     * @param status Status do agendamento
     * @return Lista de agendamentos
     */
    @Query("SELECT a FROM JpaAgendamento a " +
           "WHERE a.barbeariaId = :barbeariaId " +
           "AND a.dataHora BETWEEN :dataInicio AND :dataFim " +
           "AND a.status = :status " +
           "ORDER BY a.dataHora ASC")
    List<JpaAgendamento> findByBarbeariaIdAndDataHoraBetweenAndStatus(
            @Param("barbeariaId") Long barbeariaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("status") com.barbearia.domain.enums.StatusAgendamento status
    );
}


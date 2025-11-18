package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.FeriadoExcecaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para gerenciar exceções de horário (feriados, fechamentos especiais).
 */
@Repository
public interface FeriadoExcecaoRepository extends JpaRepository<FeriadoExcecaoEntity, Long> {
    
    /**
     * Busca todas as exceções de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @return lista de exceções
     */
    List<FeriadoExcecaoEntity> findByBarbeariaId(Long barbeariaId);
    
    /**
     * Busca todas as exceções ativas de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @param ativo status de ativo
     * @return lista de exceções ativas
     */
    List<FeriadoExcecaoEntity> findByBarbeariaIdAndAtivo(Long barbeariaId, boolean ativo);
    
    /**
     * Busca exceção por barbearia e data específica.
     * 
     * @param barbeariaId ID da barbearia
     * @param data data da exceção
     * @return exceção se existir
     */
    Optional<FeriadoExcecaoEntity> findByBarbeariaIdAndData(Long barbeariaId, LocalDate data);
    
    /**
     * Busca exceção ativa por barbearia e data específica.
     * 
     * @param barbeariaId ID da barbearia
     * @param data data da exceção
     * @param ativo status de ativo
     * @return exceção ativa se existir
     */
    Optional<FeriadoExcecaoEntity> findByBarbeariaIdAndDataAndAtivo(Long barbeariaId, LocalDate data, boolean ativo);
    
    /**
     * Busca exceções de uma barbearia em um período.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return lista de exceções no período
     */
    @Query("SELECT f FROM FeriadoExcecaoEntity f WHERE f.barbeariaId = :barbeariaId " +
           "AND f.data BETWEEN :dataInicio AND :dataFim ORDER BY f.data ASC")
    List<FeriadoExcecaoEntity> findByBarbeariaIdAndDataBetween(
        @Param("barbeariaId") Long barbeariaId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    /**
     * Busca exceções ativas de uma barbearia em um período.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio data inicial
     * @param dataFim data final
     * @param ativo status de ativo
     * @return lista de exceções ativas no período
     */
    @Query("SELECT f FROM FeriadoExcecaoEntity f WHERE f.barbeariaId = :barbeariaId " +
           "AND f.data BETWEEN :dataInicio AND :dataFim AND f.ativo = :ativo ORDER BY f.data ASC")
    List<FeriadoExcecaoEntity> findByBarbeariaIdAndDataBetweenAndAtivo(
        @Param("barbeariaId") Long barbeariaId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("ativo") boolean ativo
    );
    
    /**
     * Verifica se existe exceção para uma barbearia em uma data específica.
     * 
     * @param barbeariaId ID da barbearia
     * @param data data
     * @return true se existir
     */
    boolean existsByBarbeariaIdAndData(Long barbeariaId, LocalDate data);
    
    /**
     * Remove todas as exceções de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     */
    void deleteByBarbeariaId(Long barbeariaId);
}

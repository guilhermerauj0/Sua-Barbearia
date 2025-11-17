package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para Funcionario.
 */
@Repository
public interface FuncionarioRepository extends JpaRepository<JpaFuncionario, Long> {
    
    /**
     * Encontra um funcionário por ID (com opção de filtrar apenas ativos).
     */
    @Query("SELECT f FROM JpaFuncionario f WHERE f.id = :id AND f.ativo = true")
    Optional<JpaFuncionario> findByIdAtivo(@Param("id") Long id);
    
    /**
     * Encontra todos os funcionários ativos de uma barbearia.
     */
    @Query("SELECT f FROM JpaFuncionario f WHERE f.barbeariaId = :barbeariaId AND f.ativo = true")
    List<JpaFuncionario> findByBarbeariaIdAtivo(@Param("barbeariaId") Long barbeariaId);
    
    /**
     * Encontra todos os funcionários (ativos ou não) de uma barbearia.
     */
    @Query("SELECT f FROM JpaFuncionario f WHERE f.barbeariaId = :barbeariaId")
    List<JpaFuncionario> findByBarbeariaId(@Param("barbeariaId") Long barbeariaId);
    
    /**
     * Encontra um funcionário por email (apenas ativos).
     */
    @Query("SELECT f FROM JpaFuncionario f WHERE LOWER(f.email) = LOWER(:email) AND f.ativo = true")
    Optional<JpaFuncionario> findByEmailAtivo(@Param("email") String email);
    
    /**
     * Encontra um funcionário por telefone (apenas ativos).
     */
    @Query("SELECT f FROM JpaFuncionario f WHERE f.telefone = :telefone AND f.ativo = true")
    Optional<JpaFuncionario> findByTelefoneAtivo(@Param("telefone") String telefone);
    
    /**
     * Verifica se existe funcionário com email específico na barbearia.
     */
    boolean existsByEmailAndBarbeariaId(String email, Long barbeariaId);
    
    /**
     * Encontra funcionários ativos de uma barbearia (método alternativo).
     */
    List<JpaFuncionario> findByBarbeariaIdAndAtivoTrue(Long barbeariaId);
}

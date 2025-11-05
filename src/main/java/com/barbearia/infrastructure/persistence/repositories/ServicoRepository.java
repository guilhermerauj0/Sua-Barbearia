package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para acesso a dados de Serviços no banco de dados.
 * 
 * Fornece métodos para CRUD e queries customizadas relacionadas a serviços.
 * 
 * @author Sua Barbearia Team
 */
@Repository
public interface ServicoRepository extends JpaRepository<JpaServico, Long> {
    
    /**
     * Busca todos os serviços ativos de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de serviços ativos
     */
    List<JpaServico> findByBarbeariaIdAndAtivoTrue(Long barbeariaId);
    
    /**
     * Busca todos os serviços (ativos ou não) de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de todos os serviços
     */
    List<JpaServico> findByBarbeariaId(Long barbeariaId);
    
    /**
     * Verifica se um serviço existe para uma barbearia específica.
     * 
     * @param id ID do serviço
     * @param barbeariaId ID da barbearia
     * @return true se existe, false caso contrário
     */
    boolean existsByIdAndBarbeariaId(Long id, Long barbeariaId);
    
    /**
     * Busca um serviço ativo por ID e barbearia.
     * 
     * @param id ID do serviço
     * @param barbeariaId ID da barbearia
     * @return Serviço se encontrado
     */
    Optional<JpaServico> findByIdAndBarbeariaIdAndAtivoTrue(Long id, Long barbeariaId);
}

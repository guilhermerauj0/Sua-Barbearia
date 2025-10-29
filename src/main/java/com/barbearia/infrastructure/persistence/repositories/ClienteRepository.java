package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para acesso aos dados de Cliente no banco de dados.
 * 
 * Spring Data JPA:
 * - Interface que estende JpaRepository
 * - Spring cria automaticamente a implementação em tempo de execução
 * - Fornece métodos CRUD básicos (save, findById, delete, etc)
 * - Permite definir métodos de consulta customizados
 * 
 * Convenção de nomes:
 * - findBy[NomeDoCampo]: busca por um campo específico
 * - existsBy[NomeDoCampo]: verifica se existe registro com aquele valor
 * - countBy[NomeDoCampo]: conta registros com aquele valor
 * 
 * @author Sua Barbearia Team
 */
@Repository
public interface ClienteRepository extends JpaRepository<JpaCliente, Long> {
    
    /**
     * Busca um cliente pelo email
     * 
     * Retorna Optional para evitar NullPointerException
     * - Optional.of(cliente): quando encontra o cliente
     * - Optional.empty(): quando não encontra
     * 
     * @param email Email do cliente a ser buscado
     * @return Optional contendo o cliente se encontrado, ou vazio caso contrário
     */
    Optional<JpaCliente> findByEmail(String email);
    
    /**
     * Verifica se existe um cliente com o email informado
     * 
     * Útil para validar duplicidade de email sem precisar carregar o objeto completo
     * Mais performático que buscar e verificar se é null
     * 
     * @param email Email a ser verificado
     * @return true se existe cliente com este email, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca um cliente pelo telefone
     * 
     * @param telefone Telefone do cliente (apenas números)
     * @return Optional contendo o cliente se encontrado, ou vazio caso contrário
     */
    Optional<JpaCliente> findByTelefone(String telefone);
    
    /**
     * Verifica se existe um cliente com o telefone informado
     * 
     * @param telefone Telefone a ser verificado
     * @return true se existe cliente com este telefone, false caso contrário
     */
    boolean existsByTelefone(String telefone);
    
    /**
     * Busca um cliente pelo email que esteja ativo
     * 
     * Útil quando queremos apenas clientes ativos (soft delete)
     * 
     * @param email Email do cliente
     * @return Optional contendo o cliente ativo se encontrado, ou vazio caso contrário
     */
    Optional<JpaCliente> findByEmailAndAtivoTrue(String email);
}

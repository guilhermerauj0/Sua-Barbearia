package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    /**
     * Busca todos os clientes que foram atendidos por uma barbearia específica.
     * 
     * Query JPQL que faz join com a tabela de agendamentos para encontrar
     * clientes que possuem pelo menos um agendamento naquela barbearia.
     * Retorna apenas clientes não anonimizados.
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de clientes atendidos pela barbearia
     */
    @Query("SELECT DISTINCT c FROM JpaCliente c " +
           "INNER JOIN JpaAgendamento a ON a.clienteId = c.id " +
           "WHERE a.barbeariaId = :barbeariaId " +
           "AND c.anonimizado = false " +
           "ORDER BY c.nome ASC")
    List<JpaCliente> findClientesAtendidosPorBarbearia(@Param("barbeariaId") Long barbeariaId);
    
    /**
     * Busca um cliente específico que foi atendido por uma barbearia.
     * 
     * Verifica se o cliente pertence à carteira de clientes da barbearia
     * (teve pelo menos um agendamento).
     * 
     * @param clienteId ID do cliente
     * @param barbeariaId ID da barbearia
     * @return Optional contendo o cliente se foi atendido pela barbearia
     */
    @Query("SELECT DISTINCT c FROM JpaCliente c " +
           "INNER JOIN JpaAgendamento a ON a.clienteId = c.id " +
           "WHERE c.id = :clienteId " +
           "AND a.barbeariaId = :barbeariaId " +
           "AND c.anonimizado = false")
    Optional<JpaCliente> findClienteAtendidoPorBarbearia(
            @Param("clienteId") Long clienteId,
            @Param("barbeariaId") Long barbeariaId);
}

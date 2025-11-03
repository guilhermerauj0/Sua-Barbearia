package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.domain.enums.TipoDocumento;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para acesso aos dados de Barbearia no banco de dados.
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
 * - findBy[Campo1]And[Campo2]: busca por múltiplos campos
 * 
 * @author Sua Barbearia Team
 */
@Repository
public interface BarbeariaRepository extends JpaRepository<JpaBarbearia, Long> {
    
    /**
     * Busca uma barbearia pelo email
     * 
     * Retorna Optional para evitar NullPointerException
     * - Optional.of(barbearia): quando encontra a barbearia
     * - Optional.empty(): quando não encontra
     * 
     * @param email Email da barbearia a ser buscada
     * @return Optional contendo a barbearia se encontrada, ou vazio caso contrário
     */
    Optional<JpaBarbearia> findByEmail(String email);
    
    /**
     * Verifica se existe uma barbearia com o email informado
     * 
     * Útil para validar duplicidade de email sem precisar carregar o objeto completo
     * Mais performático que buscar e verificar se é null
     * 
     * @param email Email a ser verificado
     * @return true se existe barbearia com este email, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca uma barbearia pelo tipo de documento e número do documento
     * 
     * Permite verificar se já existe uma barbearia com aquele documento específico
     * Ex: buscar CPF 123.456.789-00 ou CNPJ 12.345.678/0001-90
     * 
     * @param tipoDocumento Tipo do documento (CPF ou CNPJ)
     * @param documento Número do documento sem formatação
     * @return Optional contendo a barbearia se encontrada, ou vazio caso contrário
     */
    Optional<JpaBarbearia> findByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);
    
    /**
     * Verifica se existe uma barbearia com o tipo de documento e número informados
     * 
     * Valida unicidade composta: não pode haver dois CPFs iguais ou dois CNPJs iguais
     * Mas pode haver um CPF e um CNPJ com os mesmos números (são tipos diferentes)
     * 
     * @param tipoDocumento Tipo do documento (CPF ou CNPJ)
     * @param documento Número do documento a ser verificado
     * @return true se existe barbearia com este documento, false caso contrário
     */
    boolean existsByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);
    
    /**
     * Busca uma barbearia pelo telefone
     * 
     * @param telefone Telefone da barbearia (apenas números)
     * @return Optional contendo a barbearia se encontrada, ou vazio caso contrário
     */
    Optional<JpaBarbearia> findByTelefone(String telefone);
    
    /**
     * Verifica se existe uma barbearia com o telefone informado
     * 
     * @param telefone Telefone a ser verificado
     * @return true se existe barbearia com este telefone, false caso contrário
     */
    boolean existsByTelefone(String telefone);
    
    /**
     * Busca uma barbearia pelo email que esteja ativa
     * 
     * Útil quando queremos apenas barbearias ativas (soft delete)
     * 
     * @param email Email da barbearia
     * @return Optional contendo a barbearia ativa se encontrada, ou vazio caso contrário
     */
    Optional<JpaBarbearia> findByEmailAndAtivoTrue(String email);
    
    /**
     * Busca uma barbearia pelo documento que esteja ativa
     * 
     * @param tipoDocumento Tipo do documento (CPF ou CNPJ)
     * @param documento Número do documento
     * @return Optional contendo a barbearia ativa se encontrada, ou vazio caso contrário
     */
    Optional<JpaBarbearia> findByTipoDocumentoAndDocumentoAndAtivoTrue(TipoDocumento tipoDocumento, String documento);
}

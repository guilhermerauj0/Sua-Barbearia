package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.domain.entities.Cliente;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;

/**
 * Mapper para conversão entre diferentes representações de Cliente.
 * 
 * Por que usar Mapper?
 * - Separa as responsabilidades de conversão
 * - Centraliza lógica de transformação de dados
 * - Facilita manutenção quando mudam os objetos
 * - Evita código duplicado de conversão
 * 
 * Conversões suportadas:
 * - Domínio (Cliente) -> JPA (JpaCliente): para persistir
 * - JPA (JpaCliente) -> Domínio (Cliente): para usar no negócio
 * - Domínio (Cliente) -> DTO Response: para retornar na API
 * - JPA (JpaCliente) -> DTO Response: atalho para API
 * 
 * @author Sua Barbearia Team
 */
public class ClienteMapper {
    
    /**
     * Converte Cliente de domínio para JpaCliente (persistência)
     * 
     * Usado quando queremos salvar um cliente do domínio no banco
     * 
     * @param cliente Cliente de domínio
     * @return JpaCliente pronto para persistir
     */
    public static JpaCliente toJpaEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(cliente.getId());
        jpaCliente.setNome(cliente.getNome());
        jpaCliente.setEmail(cliente.getEmail());
        jpaCliente.setSenha(cliente.getSenha());
        jpaCliente.setTelefone(cliente.getTelefone());
        jpaCliente.setRole(cliente.getRole());
        jpaCliente.setAtivo(cliente.isAtivo());
        jpaCliente.setDataCriacao(cliente.getDataCriacao());
        jpaCliente.setDataAtualizacao(cliente.getDataAtualizacao());
        
        return jpaCliente;
    }
    
    /**
     * Converte JpaCliente (persistência) para Cliente de domínio
     * 
     * Usado quando buscamos do banco e queremos usar no domínio
     * 
     * @param jpaCliente JpaCliente vindo do banco
     * @return Cliente de domínio
     */
    public static Cliente toDomain(JpaCliente jpaCliente) {
        if (jpaCliente == null) {
            return null;
        }
        
        Cliente cliente = new Cliente();
        cliente.setId(jpaCliente.getId());
        cliente.setNome(jpaCliente.getNome());
        cliente.setEmail(jpaCliente.getEmail());
        cliente.setSenha(jpaCliente.getSenha());
        cliente.setTelefone(jpaCliente.getTelefone());
        cliente.setRole(jpaCliente.getRole());
        cliente.setAtivo(jpaCliente.isAtivo());
        cliente.setDataCriacao(jpaCliente.getDataCriacao());
        cliente.setDataAtualizacao(jpaCliente.getDataAtualizacao());
        
        return cliente;
    }
    
    /**
     * Converte Cliente de domínio para ClienteResponseDto
     * 
     * Usado para retornar o cliente na API
     * IMPORTANTE: Não inclui a senha no DTO de resposta
     * 
     * @param cliente Cliente de domínio
     * @return ClienteResponseDto para retornar na API
     */
    public static ClienteResponseDto toResponseDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setRole(cliente.getRole());
        dto.setAtivo(cliente.isAtivo());
        dto.setDataCriacao(cliente.getDataCriacao());
        // Nota: senha não é incluída por segurança
        
        return dto;
    }
    
    /**
     * Converte JpaCliente diretamente para ClienteResponseDto
     * 
     * Atalho útil quando não precisamos passar pelo domínio
     * 
     * @param jpaCliente JpaCliente vindo do banco
     * @return ClienteResponseDto para retornar na API
     */
    public static ClienteResponseDto toResponseDto(JpaCliente jpaCliente) {
        if (jpaCliente == null) {
            return null;
        }
        
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(jpaCliente.getId());
        dto.setNome(jpaCliente.getNome());
        dto.setEmail(jpaCliente.getEmail());
        dto.setTelefone(jpaCliente.getTelefone());
        dto.setRole(jpaCliente.getRole());
        dto.setAtivo(jpaCliente.isAtivo());
        dto.setDataCriacao(jpaCliente.getDataCriacao());
        // Nota: senha não é incluída por segurança
        
        return dto;
    }
}

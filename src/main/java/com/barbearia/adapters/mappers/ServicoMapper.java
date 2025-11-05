package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.domain.entities.Servico;
import com.barbearia.infrastructure.persistence.entities.JpaServico;

/**
 * Mapper para conversão entre diferentes representações de Serviço.
 * 
 * Conversões suportadas:
 * - JPA (JpaServico) -> DTO: para retornar na API
 * - Domínio (Servico) -> JPA (JpaServico): para persistir
 * 
 * @author Sua Barbearia Team
 */
public class ServicoMapper {
    
    /**
     * Converte JpaServico para ServicoDto
     * 
     * @param jpaServico JpaServico vindo do banco
     * @return ServicoDto para retornar na API
     */
    public static ServicoDto toDto(JpaServico jpaServico) {
        if (jpaServico == null) {
            return null;
        }
        
        ServicoDto dto = new ServicoDto();
        dto.setId(jpaServico.getId());
        dto.setNome(jpaServico.getNome());
        dto.setDescricao(jpaServico.getDescricao());
        dto.setPreco(jpaServico.getPreco());
        dto.setDuracao(jpaServico.getDuracao());
        dto.setBarbeariaId(jpaServico.getBarbeariaId());
        dto.setAtivo(jpaServico.isAtivo());
        
        return dto;
    }
    
    /**
     * Converte Servico de domínio para JpaServico
     * 
     * @param servico Servico de domínio
     * @return JpaServico pronto para persistir
     */
    public static JpaServico toJpaEntity(Servico servico) {
        if (servico == null) {
            return null;
        }
        
        JpaServico jpaServico = new JpaServico();
        jpaServico.setId(servico.getId());
        jpaServico.setNome(servico.getNome());
        jpaServico.setDescricao(servico.getDescricao());
        jpaServico.setPreco(servico.getPreco());
        jpaServico.setDuracao(servico.getDuracao());
        jpaServico.setBarbeariaId(servico.getBarbeariaId());
        jpaServico.setAtivo(servico.isAtivo());
        jpaServico.setDataCriacao(servico.getDataCriacao());
        jpaServico.setDataAtualizacao(servico.getDataAtualizacao());
        
        return jpaServico;
    }
    
    /**
     * Converte JpaServico para Servico de domínio
     * 
     * @param jpaServico JpaServico vindo do banco
     * @return Servico de domínio
     */
    public static Servico toDomain(JpaServico jpaServico) {
        if (jpaServico == null) {
            return null;
        }
        
        Servico servico = new Servico();
        servico.setId(jpaServico.getId());
        servico.setNome(jpaServico.getNome());
        servico.setDescricao(jpaServico.getDescricao());
        servico.setPreco(jpaServico.getPreco());
        servico.setDuracao(jpaServico.getDuracao());
        servico.setBarbeariaId(jpaServico.getBarbeariaId());
        servico.setAtivo(jpaServico.isAtivo());
        servico.setDataCriacao(jpaServico.getDataCriacao());
        servico.setDataAtualizacao(jpaServico.getDataAtualizacao());
        
        return servico;
    }
}

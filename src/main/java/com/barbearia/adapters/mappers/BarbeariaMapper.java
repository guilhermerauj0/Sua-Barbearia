package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.domain.entities.Barbearia;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;

/**
 * Mapper para conversão entre diferentes representações de Barbearia.
 * 
 * Por que usar Mapper?
 * - Separa as responsabilidades de conversão
 * - Centraliza lógica de transformação de dados
 * - Facilita manutenção quando mudam os objetos
 * - Evita código duplicado de conversão
 * 
 * Conversões suportadas:
 * - Domínio (Barbearia) -> JPA (JpaBarbearia): para persistir
 * - JPA (JpaBarbearia) -> Domínio (Barbearia): para usar no negócio
 * - Domínio (Barbearia) -> DTO Response: para retornar na API
 * - JPA (JpaBarbearia) -> DTO Response: atalho para API
 * 
 * @author Sua Barbearia Team
 */
public class BarbeariaMapper {
    
    /**
     * Converte Barbearia de domínio para JpaBarbearia (persistência)
     * 
     * Usado quando queremos salvar uma barbearia do domínio no banco
     * 
     * @param barbearia Barbearia de domínio
     * @return JpaBarbearia pronta para persistir
     */
    public static JpaBarbearia toJpaEntity(Barbearia barbearia) {
        if (barbearia == null) {
            return null;
        }
        
        JpaBarbearia jpaBarbearia = new JpaBarbearia();
        jpaBarbearia.setId(barbearia.getId());
        jpaBarbearia.setNome(barbearia.getNome());
        jpaBarbearia.setEmail(barbearia.getEmail());
        jpaBarbearia.setSenha(barbearia.getSenha());
        jpaBarbearia.setTelefone(barbearia.getTelefone());
        jpaBarbearia.setNomeFantasia(barbearia.getNomeFantasia());
        jpaBarbearia.setTipoDocumento(barbearia.getTipoDocumento());
        jpaBarbearia.setDocumento(barbearia.getDocumento());
        jpaBarbearia.setEndereco(barbearia.getEndereco());
        jpaBarbearia.setRole(barbearia.getRole());
        jpaBarbearia.setAtivo(barbearia.isAtivo());
        jpaBarbearia.setDataCriacao(barbearia.getDataCriacao());
        jpaBarbearia.setDataAtualizacao(barbearia.getDataAtualizacao());
        
        return jpaBarbearia;
    }
    
    /**
     * Converte JpaBarbearia (persistência) para Barbearia de domínio
     * 
     * Usado quando buscamos do banco e queremos usar no domínio
     * 
     * @param jpaBarbearia JpaBarbearia vinda do banco
     * @return Barbearia de domínio
     */
    public static Barbearia toDomain(JpaBarbearia jpaBarbearia) {
        if (jpaBarbearia == null) {
            return null;
        }
        
        Barbearia barbearia = new Barbearia();
        barbearia.setId(jpaBarbearia.getId());
        barbearia.setNome(jpaBarbearia.getNome());
        barbearia.setEmail(jpaBarbearia.getEmail());
        barbearia.setSenha(jpaBarbearia.getSenha());
        barbearia.setTelefone(jpaBarbearia.getTelefone());
        barbearia.setNomeFantasia(jpaBarbearia.getNomeFantasia());
        barbearia.setTipoDocumento(jpaBarbearia.getTipoDocumento());
        barbearia.setDocumento(jpaBarbearia.getDocumento());
        barbearia.setEndereco(jpaBarbearia.getEndereco());
        barbearia.setRole(jpaBarbearia.getRole());
        barbearia.setAtivo(jpaBarbearia.isAtivo());
        barbearia.setDataCriacao(jpaBarbearia.getDataCriacao());
        barbearia.setDataAtualizacao(jpaBarbearia.getDataAtualizacao());
        
        return barbearia;
    }
    
    /**
     * Converte Barbearia de domínio para BarbeariaResponseDto
     * 
     * Usado para retornar a barbearia na API
     * IMPORTANTE: Não inclui a senha no DTO de resposta
     * 
     * @param barbearia Barbearia de domínio
     * @return BarbeariaResponseDto para retornar na API
     */
    public static BarbeariaResponseDto toResponseDto(Barbearia barbearia) {
        if (barbearia == null) {
            return null;
        }
        
        BarbeariaResponseDto dto = new BarbeariaResponseDto();
        dto.setId(barbearia.getId());
        dto.setNome(barbearia.getNome());
        dto.setEmail(barbearia.getEmail());
        dto.setTelefone(barbearia.getTelefone());
        dto.setNomeFantasia(barbearia.getNomeFantasia());
        dto.setTipoDocumento(barbearia.getTipoDocumento());
        dto.setDocumento(barbearia.getDocumento());
        dto.setEndereco(barbearia.getEndereco());
        dto.setRole(barbearia.getRole());
        dto.setAtivo(barbearia.isAtivo());
        dto.setDataCriacao(barbearia.getDataCriacao());
        // Nota: senha não é incluída por segurança
        
        return dto;
    }
    
    /**
     * Converte JpaBarbearia diretamente para BarbeariaResponseDto
     * 
     * Atalho útil quando não precisamos passar pelo domínio
     * 
     * @param jpaBarbearia JpaBarbearia vinda do banco
     * @return BarbeariaResponseDto para retornar na API
     */
    public static BarbeariaResponseDto toResponseDto(JpaBarbearia jpaBarbearia) {
        if (jpaBarbearia == null) {
            return null;
        }
        
        BarbeariaResponseDto dto = new BarbeariaResponseDto();
        dto.setId(jpaBarbearia.getId());
        dto.setNome(jpaBarbearia.getNome());
        dto.setEmail(jpaBarbearia.getEmail());
        dto.setTelefone(jpaBarbearia.getTelefone());
        dto.setNomeFantasia(jpaBarbearia.getNomeFantasia());
        dto.setTipoDocumento(jpaBarbearia.getTipoDocumento());
        dto.setDocumento(jpaBarbearia.getDocumento());
        dto.setEndereco(jpaBarbearia.getEndereco());
        dto.setRole(jpaBarbearia.getRole());
        dto.setAtivo(jpaBarbearia.isAtivo());
        dto.setDataCriacao(jpaBarbearia.getDataCriacao());
        // Nota: senha não é incluída por segurança
        
        return dto;
    }
    
    /**
     * Converte JpaBarbearia para BarbeariaListItemDto
     * 
     * Usado para retornar barbearias em listas com informações resumidas
     * 
     * @param jpaBarbearia JpaBarbearia vinda do banco
     * @return BarbeariaListItemDto com informações da barbearia
     */
    public static BarbeariaListItemDto toListItemDto(JpaBarbearia jpaBarbearia) {
        if (jpaBarbearia == null) {
            return null;
        }
        
        BarbeariaListItemDto dto = new BarbeariaListItemDto();
        dto.setId(jpaBarbearia.getId());
        dto.setNome(jpaBarbearia.getNome());
        dto.setNomeFantasia(jpaBarbearia.getNomeFantasia());
        dto.setEndereco(jpaBarbearia.getEndereco());
        dto.setTelefone(jpaBarbearia.getTelefone());
        dto.setEmail(jpaBarbearia.getEmail());
        // Para futuro: adicionar cálculo de avaliação média
        dto.setAvaliacaoMedia(0.0);
        
        return dto;
    }
}

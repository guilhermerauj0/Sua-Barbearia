package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.AgendamentoDetailDto;
import com.barbearia.domain.entities.Agendamento;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;

/**
 * Mapper para conversão entre representações de Agendamento.
 * 
 * Responsabilidades:
 * - Converter entidade JPA para entidade de domínio
 * - Converter entidade de domínio para entidade JPA
 * - Converter para DTOs (brief, detalhado, etc)
 * 
 * Por enquanto, apenas conversões básicas.
 * Conversão para AgendamentoBriefDto será enriquecida com dados de outras entidades
 * quando implementarmos Servicos e Barbeiros.
 * 
 * @author Sua Barbearia Team
 */
public class AgendamentoMapper {
    
    /**
     * Converte entidade JPA para entidade de domínio
     */
    public static Agendamento toDomain(JpaAgendamento jpa) {
        if (jpa == null) return null;
        
        return new Agendamento(
                jpa.getId(),
                jpa.getClienteId(),
                jpa.getBarbeariaId(),
                jpa.getBarbeiroId(),
                jpa.getServicoId(),
                jpa.getDataHora(),
                jpa.getStatus(),
                jpa.getObservacoes(),
                jpa.getDataCriacao(),
                jpa.getDataAtualizacao()
        );
    }
    
    /**
     * Converte entidade de domínio para entidade JPA
     */
    public static JpaAgendamento toJpa(Agendamento domain) {
        if (domain == null) return null;
        
        JpaAgendamento jpa = new JpaAgendamento();
        jpa.setId(domain.getId());
        jpa.setClienteId(domain.getClienteId());
        jpa.setBarbeariaId(domain.getBarbeariaId());
        jpa.setBarbeiroId(domain.getBarbeiroId());
        jpa.setServicoId(domain.getServicoId());
        jpa.setDataHora(domain.getDataHora());
        jpa.setStatus(domain.getStatus());
        jpa.setObservacoes(domain.getObservacoes());
        jpa.setDataCriacao(domain.getDataCriacao());
        jpa.setDataAtualizacao(domain.getDataAtualizacao());
        
        return jpa;
    }
    
    /**
     * Converte entidade JPA para DTO resumido.
     * 
     * NOTA: Por enquanto, nomeBarbearia, nomeBarbeiro e nomeServico são placeholders.
     * Quando implementarmos as entidades correspondentes, faremos joins ou buscas
     * para preencher esses dados reais.
     */
    public static AgendamentoBriefDto toBriefDto(JpaAgendamento jpa) {
        if (jpa == null) return null;
        
        return new AgendamentoBriefDto(
                jpa.getId(),
                jpa.getDataHora(),
                jpa.getStatus(),
                "Barbearia #" + jpa.getBarbeariaId(), // Placeholder - substituir com nome real
                jpa.getBarbeiroId() != null ? "Barbeiro #" + jpa.getBarbeiroId() : null, // Placeholder
                "Serviço #" + jpa.getServicoId(), // Placeholder - substituir com nome real
                jpa.getObservacoes()
        );
    }
    
    /**
     * Converte entidade de domínio para DTO resumido
     */
    public static AgendamentoBriefDto toBriefDto(Agendamento domain) {
        if (domain == null) return null;
        
        return new AgendamentoBriefDto(
                domain.getId(),
                domain.getDataHora(),
                domain.getStatus(),
                "Barbearia #" + domain.getBarbeariaId(), // Placeholder
                domain.getBarbeiroId() != null ? "Barbeiro #" + domain.getBarbeiroId() : null,
                "Serviço #" + domain.getServicoId(), // Placeholder
                domain.getObservacoes()
        );
    }
    
    /**
     * Converte entidade JPA para DTO detalhado.
     * 
     * NOTA: Por enquanto, os dados de Cliente, Barbearia, Barbeiro e Serviço são placeholders.
     * Quando implementarmos as entidades correspondentes com relacionamentos JPA,
     * faremos joins ou buscas para preencher esses dados reais.
     */
    public static AgendamentoDetailDto toDetailDto(JpaAgendamento jpa) {
        if (jpa == null) return null;
        
        return new AgendamentoDetailDto(
                // Dados do agendamento
                jpa.getId(),
                jpa.getDataHora(),
                jpa.getStatus(),
                jpa.getObservacoes(),
                jpa.getDataCriacao(),
                jpa.getDataAtualizacao(),
                
                // Dados do cliente (placeholders)
                jpa.getClienteId(),
                "Cliente #" + jpa.getClienteId(),
                "email@cliente.com",
                "(00) 0000-0000",
                "00000000000",
                
                // Dados da barbearia (placeholders)
                jpa.getBarbeariaId(),
                "Barbearia #" + jpa.getBarbeariaId(),
                "Rua Exemplo, 123",
                "(00) 0000-0000",
                
                // Dados do barbeiro (pode ser null)
                jpa.getBarbeiroId(),
                jpa.getBarbeiroId() != null ? "Barbeiro #" + jpa.getBarbeiroId() : null,
                
                // Dados do serviço (placeholders)
                jpa.getServicoId(),
                "Serviço #" + jpa.getServicoId(),
                "Descrição do serviço",
                0.0
        );
    }
}

package com.barbearia.application.services;

import com.barbearia.adapters.mappers.AgendamentoMapper;
import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de Agendamentos.
 * 
 * Responsabilidades:
 * - Buscar histórico de agendamentos de um cliente
 * - Buscar agendamentos futuros de um cliente
 * - Criar novos agendamentos (futuro - T7)
 * - Atualizar status de agendamentos (futuro)
 * 
 * Regras de negócio:
 * - Cliente só pode ver seus próprios agendamentos
 * - Histórico contém apenas agendamentos passados (dataHora < now)
 * - Ordenação por data decrescente (mais recente primeiro)
 * 
 * @author Sua Barbearia Team
 */
@Service
public class AgendamentoService {
    
    private final AgendamentoRepository agendamentoRepository;
    
    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }
    
    /**
     * Lista o histórico de agendamentos de um cliente.
     * 
     * Retorna apenas agendamentos com dataHora < now()
     * Ordenados por data decrescente (mais recente primeiro)
     * 
     * @param clienteId ID do cliente autenticado
     * @return Lista de agendamentos passados em formato resumido
     */
    public List<AgendamentoBriefDto> listarHistoricoCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        
        LocalDateTime agora = LocalDateTime.now();
        
        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findHistoricoByClienteId(clienteId, agora);
        
        return agendamentos.stream()
                .map(AgendamentoMapper::toBriefDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lista agendamentos futuros de um cliente.
     * 
     * @param clienteId ID do cliente autenticado
     * @return Lista de agendamentos futuros em formato resumido
     */
    public List<AgendamentoBriefDto> listarAgendamentosFuturos(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        
        LocalDateTime agora = LocalDateTime.now();
        
        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findAgendamentosFuturosByClienteId(clienteId, agora);
        
        return agendamentos.stream()
                .map(AgendamentoMapper::toBriefDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lista todos os agendamentos de um cliente (histórico + futuros)
     * 
     * @param clienteId ID do cliente autenticado
     * @return Lista completa de agendamentos
     */
    public List<AgendamentoBriefDto> listarTodosAgendamentosCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        
        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findByClienteIdOrderByDataHoraDesc(clienteId);
        
        return agendamentos.stream()
                .map(AgendamentoMapper::toBriefDto)
                .collect(Collectors.toList());
    }
}

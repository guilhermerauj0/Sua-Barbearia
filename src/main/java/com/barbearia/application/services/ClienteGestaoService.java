package com.barbearia.application.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbearia.application.dto.ClienteAtendidoDto;
import com.barbearia.application.dto.ClienteDetalhesDto;
import com.barbearia.application.dto.ClienteDetalhesDto.AgendamentoHistoricoDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.ClienteNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;

/**
 * Serviço responsável pela gestão de clientes atendidos pela barbearia.
 * 
 * <p>Funcionalidades principais:</p>
 * <ul>
 *   <li>Listagem de clientes atendidos</li>
 *   <li>Visualização de detalhes de cliente</li>
 *   <li>Anonimização de dados (LGPD)</li>
 * </ul>
 * 
 * <p>Regras de LGPD (Lei Geral de Proteção de Dados):</p>
 * <ul>
 *   <li>Anonimização preserva histórico de transações (obrigação legal)</li>
 *   <li>Dados pessoais são substituídos por tokens únicos</li>
 *   <li>Cliente é marcado com flag anonimizado e data de exclusão</li>
 *   <li>Dados anonimizados não podem ser reidentificados</li>
 * </ul>
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class ClienteGestaoService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteGestaoService.class);
    
    private final ClienteRepository clienteRepository;
    private final AgendamentoRepository agendamentoRepository;
    
    public ClienteGestaoService(ClienteRepository clienteRepository, 
                               AgendamentoRepository agendamentoRepository) {
        this.clienteRepository = clienteRepository;
        this.agendamentoRepository = agendamentoRepository;
    }
    
    /**
     * Lista todos os clientes atendidos por uma barbearia.
     * 
     * <p>Retorna apenas clientes que possuem pelo menos um agendamento com a barbearia.</p>
     * <p>Clientes anonimizados não aparecem na listagem.</p>
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de DTOs com dados dos clientes atendidos
     * @throws IllegalArgumentException se barbeariaId for nulo
     */
    public List<ClienteAtendidoDto> listarClientesAtendidos(Long barbeariaId) {
        logger.info("Listando clientes atendidos pela barbearia {}", barbeariaId);
        
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        
        List<JpaCliente> clientes = clienteRepository.findClientesAtendidosPorBarbearia(barbeariaId);
        
        List<ClienteAtendidoDto> clientesDto = new ArrayList<>();
        
        for (JpaCliente cliente : clientes) {
            // Buscar estatísticas de agendamentos do cliente
            List<JpaAgendamento> agendamentos = agendamentoRepository.findByClienteIdOrderByDataHoraDesc(cliente.getId());
            
            // Filtrar apenas agendamentos desta barbearia
            List<JpaAgendamento> agendamentosBarbearia = agendamentos.stream()
                    .filter(a -> a.getBarbeariaId().equals(barbeariaId))
                    .collect(Collectors.toList());
            
            Long totalAgendamentos = (long) agendamentosBarbearia.size();
            LocalDateTime ultimoAgendamento = agendamentosBarbearia.isEmpty() ? 
                    null : agendamentosBarbearia.get(0).getDataHora();
            
            clientesDto.add(new ClienteAtendidoDto(
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getTelefone(),
                    cliente.getEmail(),
                    totalAgendamentos,
                    ultimoAgendamento,
                    cliente.isAtivo(),
                    cliente.isAnonimizado()
            ));
        }
        
        logger.info("Encontrados {} clientes atendidos", clientesDto.size());
        
        return clientesDto;
    }
    
    /**
     * Busca detalhes completos de um cliente atendido.
     * 
     * <p>Inclui histórico completo de agendamentos com a barbearia.</p>
     * <p>Apenas a barbearia que atendeu o cliente pode visualizar seus dados.</p>
     * 
     * @param clienteId ID do cliente
     * @param barbeariaId ID da barbearia
     * @return DTO com detalhes completos do cliente
     * @throws ClienteNaoEncontradoException se cliente não existir ou não foi atendido pela barbearia
     * @throws IllegalArgumentException se algum parâmetro for nulo
     */
    public ClienteDetalhesDto buscarDetalhesCliente(Long clienteId, Long barbeariaId) {
        logger.info("Buscando detalhes do cliente {} para barbearia {}", clienteId, barbeariaId);
        
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        
        // Verificar se cliente foi atendido pela barbearia
        JpaCliente cliente = clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(
                        "Cliente não encontrado ou não foi atendido por esta barbearia"));
        
        // Buscar todos os agendamentos do cliente
        List<JpaAgendamento> todosAgendamentos = agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId);
        
        // Filtrar agendamentos desta barbearia
        List<JpaAgendamento> agendamentosBarbearia = todosAgendamentos.stream()
                .filter(a -> a.getBarbeariaId().equals(barbeariaId))
                .collect(Collectors.toList());
        
        // Calcular estatísticas
        Long totalAgendamentos = (long) agendamentosBarbearia.size();
        Long agendamentosConcluidos = agendamentosBarbearia.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.CONCLUIDO)
                .count();
        Long agendamentosCancelados = agendamentosBarbearia.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.CANCELADO)
                .count();
        
        LocalDateTime primeiroAgendamento = agendamentosBarbearia.isEmpty() ? 
                null : agendamentosBarbearia.get(agendamentosBarbearia.size() - 1).getDataHora();
        LocalDateTime ultimoAgendamento = agendamentosBarbearia.isEmpty() ? 
                null : agendamentosBarbearia.get(0).getDataHora();
        
        // Converter agendamentos para DTO
        List<AgendamentoHistoricoDto> agendamentosDto = agendamentosBarbearia.stream()
                .map(a -> new AgendamentoHistoricoDto(
                        a.getId(),
                        a.getDataHora(),
                        a.getStatus().name(),
                        "Serviço " + a.getServicoId(), // Placeholder - idealmente buscar nome do serviço
                        "Funcionário " + a.getBarbeiroId(), // Placeholder - idealmente buscar nome do funcionário
                        a.getObservacoes()
                ))
                .collect(Collectors.toList());
        
        return new ClienteDetalhesDto(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                null, // Documento não está na entidade atual
                null, // Endereço não está na entidade atual
                totalAgendamentos,
                agendamentosConcluidos,
                agendamentosCancelados,
                primeiroAgendamento,
                ultimoAgendamento,
                cliente.isAtivo(),
                cliente.isAnonimizado(),
                cliente.getDataCriacao(),
                cliente.getDataAtualizacao(),
                agendamentosDto
        );
    }
    
    /**
     * Anonimiza os dados pessoais de um cliente (LGPD).
     * 
     * <p><b>Processo de anonimização:</b></p>
     * <ol>
     *   <li>Verifica se cliente pertence à barbearia</li>
     *   <li>Gera tokens únicos e irreversíveis para substituir dados pessoais</li>
     *   <li>Substitui: nome, email, telefone, senha</li>
     *   <li>Marca cliente como anonimizado e inativo</li>
     *   <li>Registra data/hora da anonimização</li>
     *   <li>Preserva histórico de agendamentos (obrigação legal)</li>
     * </ol>
     * 
     * <p><b>Importante:</b> Dados anonimizados não podem ser recuperados ou reidentificados.</p>
     * 
     * @param clienteId ID do cliente a ser anonimizado
     * @param barbeariaId ID da barbearia solicitante
     * @throws ClienteNaoEncontradoException se cliente não existir ou não pertencer à barbearia
     * @throws AcessoNegadoException se cliente já estiver anonimizado
     * @throws IllegalArgumentException se algum parâmetro for nulo
     */
    @Transactional
    public void anonimizarCliente(Long clienteId, Long barbeariaId) {
        logger.info("Iniciando anonimização LGPD do cliente {} por solicitação da barbearia {}", 
                clienteId, barbeariaId);
        
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        
        // Verificar se cliente pertence à barbearia
        JpaCliente cliente = clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(
                        "Cliente não encontrado ou não foi atendido por esta barbearia"));
        
        // Verificar se já está anonimizado
        if (cliente.isAnonimizado()) {
            throw new AcessoNegadoException("Cliente já foi anonimizado anteriormente");
        }
        
        // Gerar tokens únicos e irreversíveis
        String token = "ANONIMIZADO_" + UUID.randomUUID().toString();
        LocalDateTime agora = LocalDateTime.now();
        
        logger.info("Anonimizando dados do cliente {} - Token: {}", clienteId, token);
        
        // Substituir dados pessoais por tokens
        cliente.setNome("Cliente Anonimizado " + clienteId);
        cliente.setEmail(token + "@anonimizado.lgpd");
        cliente.setTelefone(token.substring(0, 20)); // Limite de 20 caracteres
        cliente.setSenha("SENHA_ANONIMIZADA_" + token);
        
        // Marcar como anonimizado
        cliente.setAnonimizado(true);
        cliente.setAtivo(false);
        cliente.setDeletedAt(agora);
        
        // Salvar alterações
        clienteRepository.save(cliente);
        
        logger.warn("Cliente {} anonimizado com sucesso. Dados pessoais foram substituídos por tokens. " +
                "Histórico de agendamentos preservado para compliance legal.", clienteId);
        logger.info("Data da anonimização: {}", agora);
    }
}

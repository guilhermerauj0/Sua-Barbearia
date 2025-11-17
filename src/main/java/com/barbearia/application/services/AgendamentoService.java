package com.barbearia.application.services;

import com.barbearia.adapters.mappers.AgendamentoMapper;
import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.AgendamentoDetailDto;
import com.barbearia.application.dto.AgendamentoRequestDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final FuncionarioRepository funcionarioRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalServicoRepository profissionalServicoRepository;
    
    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                             FuncionarioRepository funcionarioRepository,
                             ServicoRepository servicoRepository,
                             ProfissionalServicoRepository profissionalServicoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.servicoRepository = servicoRepository;
        this.profissionalServicoRepository = profissionalServicoRepository;
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
    
    /**
     * Busca um agendamento específico por ID com verificação de autorização.
     * 
     * Implementa a lógica centralizada de autorização:
     * - Cliente só pode ver seus próprios agendamentos
     * - Barbearia pode ver agendamentos associados a ela (futuro)
     * - Barbeiro pode ver agendamentos onde ele é o prestador (futuro)
     * 
     * Regras de negócio:
     * - Retorna 404 se o agendamento não existe
     * - Retorna 403 se o usuário não tem permissão (não é proprietário)
     * 
     * @param agendamentoId ID do agendamento a ser buscado
     * @param usuarioId ID do usuário autenticado (cliente)
     * @param tipoUsuario Tipo do usuário (CLIENTE, BARBEARIA, BARBEIRO)
     * @return DTO detalhado do agendamento
     * @throws IllegalArgumentException se usuarioId é nulo
     * @throws RuntimeException se agendamento não existe (404) ou sem permissão (403)
     */
    public AgendamentoDetailDto buscarAgendamentoPorId(Long agendamentoId, Long usuarioId, String tipoUsuario) {
        if (agendamentoId == null) {
            throw new IllegalArgumentException("ID do agendamento não pode ser nulo");
        }
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        
        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            throw new IllegalArgumentException("Tipo do usuário não pode ser nulo");
        }
        
        // Busca o agendamento no banco de dados
        Optional<JpaAgendamento> agendamento = agendamentoRepository.findById(agendamentoId);
        
        // Se não existe, retorna 404
        if (agendamento.isEmpty()) {
            throw new AgendamentoNaoEncontradoException("Agendamento com ID " + agendamentoId + " não existe");
        }
        
        JpaAgendamento jpaAgendamento = agendamento.get();
        
        // Verifica autorização
        if (!verificarAutorizacaoAcesso(jpaAgendamento, usuarioId, tipoUsuario)) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este agendamento");
        }
        
        // Retorna o DTO detalhado
        return AgendamentoMapper.toDetailDto(jpaAgendamento);
    }
    
    /**
     * Cria um novo agendamento.
     * 
     * Validações:
     * - Serviço deve existir
     * - Funcionário deve existir
     * - Funcionário deve executar o serviço (ProfissionalServico)
     * - Não deve haver conflito de horário
     * - Data/hora não pode ser no passado
     * 
     * @param clienteId ID do cliente (proprietário do agendamento)
     * @param requestDto Dados de requisição (servicoId, funcionarioId, dataHora, observacoes)
     * @return DTO de resposta com os dados do agendamento criado
     * @throws IllegalArgumentException se validações falharem
     */
    public AgendamentoResponseDto criarAgendamento(Long clienteId, AgendamentoRequestDto requestDto) {
        // Validação básica
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        
        if (requestDto == null) {
            throw new IllegalArgumentException("Dados do agendamento não podem ser nulos");
        }
        
        if (requestDto.getServicoId() == null) {
            throw new IllegalArgumentException("ID do serviço não pode ser nulo");
        }
        
        if (requestDto.getFuncionarioId() == null) {
            throw new IllegalArgumentException("ID do funcionário não pode ser nulo");
        }
        
        if (requestDto.getDataHora() == null) {
            throw new IllegalArgumentException("Data/hora do agendamento não pode ser nula");
        }
        
        // Validação: data/hora não pode ser no passado
        if (requestDto.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data/hora do agendamento não pode ser no passado");
        }
        
        // Busca o serviço
        @SuppressWarnings("null")
        Optional<JpaServico> servicoOpt = servicoRepository.findById(requestDto.getServicoId());
        if (servicoOpt.isEmpty()) {
            throw new IllegalArgumentException("Serviço com ID " + requestDto.getServicoId() + " não existe");
        }
        
        // Valida que o serviço existe (apenas verifica, não usa a referência)
        servicoOpt.get();
        
        // Busca o funcionário
        @SuppressWarnings("null")
        Optional<JpaFuncionario> funcionarioOpt = funcionarioRepository.findById(requestDto.getFuncionarioId());
        if (funcionarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Funcionário com ID " + requestDto.getFuncionarioId() + " não existe");
        }
        
        JpaFuncionario funcionario = funcionarioOpt.get();
        
        // Validação: funcionário deve executar o serviço
        if (!profissionalServicoRepository.canPrestarServico(
                requestDto.getFuncionarioId(),
                requestDto.getServicoId())) {
            throw new IllegalArgumentException("Funcionário não executa este serviço");
        }
        
        // Validação: verificar conflito de horário
        if (agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(
                requestDto.getFuncionarioId(),
                requestDto.getDataHora())) {
            throw new IllegalArgumentException("Horário não disponível para este funcionário");
        }
        
        // Cria novo agendamento
        JpaAgendamento novoAgendamento = new JpaAgendamento();
        novoAgendamento.setClienteId(clienteId);
        novoAgendamento.setServicoId(requestDto.getServicoId());
        novoAgendamento.setBarbeiroId(requestDto.getFuncionarioId());
        novoAgendamento.setBarbeariaId(funcionario.getBarbeariaId());
        novoAgendamento.setDataHora(requestDto.getDataHora());
        novoAgendamento.setObservacoes(requestDto.getObservacoes() != null ? requestDto.getObservacoes() : "");
        novoAgendamento.setStatus(StatusAgendamento.PENDENTE);
        novoAgendamento.setDataCriacao(LocalDateTime.now());
        novoAgendamento.setDataAtualizacao(LocalDateTime.now());
        
        // Salva no banco de dados
        JpaAgendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);
        
        // Retorna DTO de resposta
        return new AgendamentoResponseDto(
                agendamentoSalvo.getId(),
                agendamentoSalvo.getClienteId(),
                agendamentoSalvo.getBarbeariaId(),
                agendamentoSalvo.getServicoId(),
                agendamentoSalvo.getBarbeiroId(),
                agendamentoSalvo.getDataHora(),
                agendamentoSalvo.getStatus().toString(),
                agendamentoSalvo.getObservacoes(),
                agendamentoSalvo.getDataCriacao(),
                agendamentoSalvo.getDataAtualizacao()
        );
    }
    
    /**
     * Verifica se o usuário tem autorização para acessar um agendamento.
     * 
     * Lógica centralizada de autorização:
     * - Cliente pode acessar apenas seus próprios agendamentos
     * - Barbearia pode acessar agendamentos da sua barbearia
     * - Barbeiro pode acessar agendamentos onde é o prestador
     * 
     * @param jpaAgendamento Agendamento a ser verificado
     * @param usuarioId ID do usuário autenticado
     * @param tipoUsuario Tipo do usuário (CLIENTE, BARBEARIA, BARBEIRO)
     * @return true se tem autorização, false caso contrário
     */
    private boolean verificarAutorizacaoAcesso(JpaAgendamento jpaAgendamento, Long usuarioId, String tipoUsuario) {
        // Cliente só pode ver seus próprios agendamentos
        if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
            return jpaAgendamento.getClienteId().equals(usuarioId);
        }
        
        // Barbearia pode ver agendamentos associados a ela
        if ("BARBEARIA".equalsIgnoreCase(tipoUsuario)) {
            return jpaAgendamento.getBarbeariaId().equals(usuarioId);
        }
        
        // Barbeiro pode ver agendamentos onde é o prestador
        if ("BARBEIRO".equalsIgnoreCase(tipoUsuario)) {
            return jpaAgendamento.getBarbeiroId() != null && jpaAgendamento.getBarbeiroId().equals(usuarioId);
        }
        
        // Tipo de usuário desconhecido
        return false;
    }
}

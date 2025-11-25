package com.barbearia.application.services;

import com.barbearia.adapters.mappers.AgendamentoMapper;
import com.barbearia.application.dto.AgendamentoBarbeariaDto;
import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.AgendamentoRequestDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.observers.AgendamentoEventObserver;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de Agendamentos.
 * 
 * Responsabilidades:
 * - Buscar histórico de agendamentos de um cliente
 * - Buscar agendamentos futuros de um cliente
 * - Criar novos agendamentos
 * - Buscar agendamento por ID com verificação de autorização
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
    private final ClienteRepository clienteRepository;
    private final ProfissionalServicoRepository profissionalServicoRepository;
    private final List<AgendamentoEventObserver> eventObservers;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
            FuncionarioRepository funcionarioRepository,
            ServicoRepository servicoRepository,
            ClienteRepository clienteRepository,
            ProfissionalServicoRepository profissionalServicoRepository,
            ProfissionalLinkService profissionalLinkService,
            List<AgendamentoEventObserver> eventObservers) {
        this.agendamentoRepository = agendamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.profissionalServicoRepository = profissionalServicoRepository;
        this.eventObservers = eventObservers != null ? eventObservers : new ArrayList<>();
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
     * Lista agendamentos futuros criados recentemente (últimos N dias).
     * 
     * @param clienteId ID do cliente autenticado
     * @param dias      Número de dias para considerar como "recente"
     * @return Lista de agendamentos futuros criados nos últimos N dias, ordenados
     *         por dataCriacao DESC
     */
    public List<AgendamentoBriefDto> listarAgendamentosRecentesFuturos(Long clienteId, int dias) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        if (dias <= 0) {
            throw new IllegalArgumentException("Número de dias deve ser positivo");
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataLimite = agora.minusDays(dias);

        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findRecentesFuturosByClienteId(clienteId, agora, dataLimite);

        return agendamentos.stream()
                .map(AgendamentoMapper::toBriefDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista agendamentos concluídos recentemente (últimos N dias).
     * 
     * @param clienteId ID do cliente autenticado
     * @param dias      Número de dias
     * @return Lista de agendamentos concluídos nos últimos N dias, ordenados por
     *         dataHora DESC
     */
    public List<AgendamentoBriefDto> listarAgendamentosConcluídosRecentes(Long clienteId, int dias) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        if (dias <= 0) {
            throw new IllegalArgumentException("Número de dias deve ser positivo");
        }

        LocalDateTime dataLimite = LocalDateTime.now().minusDays(dias);

        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findConcluidosRecentesByClienteId(clienteId, dataLimite, StatusAgendamento.CONCLUIDO);

        return agendamentos.stream()
                .map(AgendamentoMapper::toBriefDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista agendamentos futuros de um profissional.
     * 
     * @param profissionalId ID do profissional autenticado
     * @return Lista de agendamentos futuros em formato resumido
     */
    public List<AgendamentoBriefDto> listarAgendamentosProfissional(Long profissionalId) {
        if (profissionalId == null) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo");
        }

        LocalDateTime agora = LocalDateTime.now();

        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findByBarbeiroIdAndDataBetween(profissionalId, agora, agora.plusYears(1));

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
     * @param usuarioId     ID do usuário autenticado (cliente)
     * @param tipoUsuario   Tipo do usuário (CLIENTE, BARBEARIA, BARBEIRO)
     * @return DTO com dados completos do agendamento
     * @throws IllegalArgumentException          se usuarioId é nulo
     * @throws AgendamentoNaoEncontradoException se agendamento não existe (404)
     * @throws AcessoNegadoException             se sem permissão (403)
     */
    public AgendamentoResponseDto buscarAgendamentoPorId(Long agendamentoId, Long usuarioId, String tipoUsuario) {
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

        // Retorna o DTO
        return AgendamentoMapper.toResponseDto(jpaAgendamento);
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
     * @param clienteId  ID do cliente (proprietário do agendamento)
     * @param requestDto Dados de requisição (servicoId, funcionarioId, dataHora,
     *                   observacoes)
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

        // Notificar criação do agendamento
        notificarCriacaoAgendamento(agendamentoSalvo, clienteId, requestDto.getServicoId(), funcionario);

        // Retorna DTO de resposta
        return AgendamentoMapper.toResponseDto(agendamentoSalvo);
    }

    /**
     * Repete um agendamento concluído CRIANDO UM NOVO registro.
     * Não modifica o histórico original.
     * 
     * @param agendamentoOriginalId ID do agendamento concluído
     * @param novaDataHora          Nova data/hora para o agendamento
     * @param usuarioId             ID do usuário solicitante
     * @param tipoUsuario           Tipo do usuário (CLIENTE, BARBEARIA, BARBEIRO)
     * @return Novo agendamento criado
     * @throws AgendamentoNaoEncontradoException se agendamento original não existe
     * @throws AcessoNegadoException             se usuário não tem permissão
     * @throws IllegalArgumentException          se agendamento não está concluído
     *                                           ou data inválida
     */
    @Transactional
    public AgendamentoResponseDto repetirAgendamento(
            Long agendamentoOriginalId,
            LocalDateTime novaDataHora,
            Long usuarioId,
            String tipoUsuario) {

        // Validar parâmetros
        if (agendamentoOriginalId == null) {
            throw new IllegalArgumentException("ID do agendamento não pode ser nulo");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }

        // Buscar agendamento original
        JpaAgendamento original = agendamentoRepository.findById(agendamentoOriginalId)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException(
                        "Agendamento original não encontrado"));

        // Verificar autorização
        if (!verificarAutorizacaoAcesso(original, usuarioId, tipoUsuario)) {
            throw new AcessoNegadoException("Sem permissão para repetir este agendamento");
        }

        // Validar que está concluído
        if (original.getStatus() != StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException(
                    "Apenas agendamentos concluídos podem ser repetidos. " +
                            "Status atual: " + original.getStatus());
        }

        // Validar nova data
        if (novaDataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nova data/hora não pode ser no passado");
        }

        // Verificar conflito de horário
        if (agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(
                original.getBarbeiroId(), novaDataHora)) {
            throw new IllegalArgumentException("Horário não disponível para este funcionário");
        }

        // CRIAR NOVO AGENDAMENTO (não modificar o original)
        JpaAgendamento novoAgendamento = new JpaAgendamento();
        novoAgendamento.setClienteId(original.getClienteId());
        novoAgendamento.setServicoId(original.getServicoId());
        novoAgendamento.setBarbeiroId(original.getBarbeiroId());
        novoAgendamento.setBarbeariaId(original.getBarbeariaId());
        novoAgendamento.setDataHora(novaDataHora); // NOVA DATA
        novoAgendamento.setObservacoes(original.getObservacoes());
        novoAgendamento.setStatus(StatusAgendamento.PENDENTE); // NOVO STATUS
        novoAgendamento.setDataCriacao(LocalDateTime.now()); // NOVA DATA DE CRIAÇÃO
        novoAgendamento.setDataAtualizacao(LocalDateTime.now());

        JpaAgendamento salvo = agendamentoRepository.save(novoAgendamento);

        return AgendamentoMapper.toResponseDto(salvo);
    }

    /**
     * Cria agendamento direto pela barbearia (cliente sem conta ou existente).
     * Busca cliente por telefone ou cria automaticamente se não existir.
     * 
     * @param barbeariaId ID da barbearia autenticada
     * @param dto         Dados do agendamento direto
     * @return Agendamento criado
     */
    @Transactional
    public AgendamentoResponseDto criarAgendamentoDireto(
            Long barbeariaId,
            com.barbearia.application.dto.AgendamentoDiretoRequestDto dto) {

        // Validações básicas
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Dados do agendamento não podem ser nulos");
        }
        if (dto.getDataHora() == null) {
            throw new IllegalArgumentException("Data/hora não pode ser nula");
        }
        if (dto.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data/hora não pode ser no passado");
        }
        if (dto.getFuncionarioId() == null) {
            throw new IllegalArgumentException("ID do funcionário não pode ser nulo");
        }
        if (dto.getServicoId() == null) {
            throw new IllegalArgumentException("ID do serviço não pode ser nulo");
        }

        // Verificar que funcionário pertence à barbearia
        // Objects.requireNonNull garante ao compilador que não é null
        Long funcionarioId = java.util.Objects.requireNonNull(dto.getFuncionarioId());
        Long servicoId = java.util.Objects.requireNonNull(dto.getServicoId());

        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        // Verificar que serviço existe e pertence à barbearia
        JpaServico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        if (!servico.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Serviço não pertence a esta barbearia");
        }

        // Verificar que funcionário pode fazer o serviço
        if (!profissionalServicoRepository.canPrestarServico(funcionarioId, servicoId)) {
            throw new IllegalArgumentException("Profissional não executa este serviço");
        }

        // Verificar conflito de horário
        if (agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(
                dto.getFuncionarioId(), dto.getDataHora())) {
            throw new IllegalArgumentException("Horário não disponível para este profissional");
        }

        // Buscar ou criar cliente
        Long clienteId;
        Optional<JpaCliente> clienteOpt = clienteRepository.findByTelefone(dto.getClienteTelefone());

        if (clienteOpt.isPresent()) {
            // Cliente já existe
            clienteId = clienteOpt.get().getId();
        } else {
            // Criar novo cliente
            JpaCliente novoCliente = new JpaCliente();
            novoCliente.setNome(dto.getClienteNome());
            novoCliente.setTelefone(dto.getClienteTelefone());

            // Email: usar fornecido ou criar placeholder
            if (dto.getClienteEmail() != null && !dto.getClienteEmail().isBlank()) {
                novoCliente.setEmail(dto.getClienteEmail());
            } else {
                // Criar email placeholder único
                novoCliente.setEmail(dto.getClienteTelefone() + "@placeholder.local");
            }

            // Senha aleatória (cliente precisará usar "esqueci senha")
            novoCliente.setSenha(java.util.UUID.randomUUID().toString());
            novoCliente.setAtivo(true);
            novoCliente.setAnonimizado(false);

            JpaCliente clienteSalvo = clienteRepository.save(novoCliente);
            clienteId = clienteSalvo.getId();
        }

        // Criar agendamento
        JpaAgendamento agendamento = new JpaAgendamento();
        agendamento.setClienteId(clienteId);
        agendamento.setServicoId(dto.getServicoId());
        agendamento.setBarbeiroId(dto.getFuncionarioId());
        agendamento.setBarbeariaId(barbeariaId);
        agendamento.setDataHora(dto.getDataHora());
        agendamento.setObservacoes(dto.getObservacoes() != null ? dto.getObservacoes() : "");
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setDataAtualizacao(LocalDateTime.now());

        JpaAgendamento salvo = agendamentoRepository.save(agendamento);

        return AgendamentoMapper.toResponseDto(salvo);
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
     * @param usuarioId      ID do usuário autenticado
     * @param tipoUsuario    Tipo do usuário (CLIENTE, BARBEARIA, BARBEIRO)
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

    /**
     * Lista agendamentos da barbearia, opcionalmente filtrados por data.
     * 
     * @param barbeariaId ID da barbearia autenticada
     * @param data        Data para filtrar (opcional - se null, retorna todos)
     * @return Lista de agendamentos detalhados com informações de cliente, serviço
     *         e funcionário
     */
    public List<AgendamentoBarbeariaDto> listarAgendamentosBarbearia(Long barbeariaId, LocalDate data) {
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }

        List<JpaAgendamento> agendamentos;

        if (data != null) {
            // Filtrar por data específica
            LocalDateTime inicioDia = data.atStartOfDay();
            LocalDateTime fimDia = data.atTime(LocalTime.MAX);
            agendamentos = agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenOrderByDataHoraAsc(
                    barbeariaId, inicioDia, fimDia);
        } else {
            // Retornar todos os agendamentos
            agendamentos = agendamentoRepository.findByBarbeariaIdOrderByDataHoraDesc(barbeariaId);
        }

        // Converter para DTOs detalhados
        return agendamentos.stream()
                .map(this::converterParaBarbeariaDto)
                .collect(Collectors.toList());
    }

    /**
     * statusNovo,
     * agendamento.getClienteId(),
     * barbeariaId);
     * 
     * // Notificar eventos específicos se houver mudança para status relevante
     * if (statusNovo == StatusAgendamento.CONFIRMADO && statusAnterior !=
     * StatusAgendamento.CONFIRMADO) {
     * notificarEventoEspecifico(agendamentoAtualizado, "confirmado", null);
     * } else if (statusNovo == StatusAgendamento.CANCELADO && statusAnterior !=
     * StatusAgendamento.CANCELADO) {
     * notificarEventoEspecifico(agendamentoAtualizado, "cancelado", null);
     * }
     * 
     * return AgendamentoMapper.toResponseDto(agendamentoAtualizado);
     * }
     * 
     * /**
     * cliente.getNome(),
     * cliente.getTelefone(),
     * servico.getNome(),
     * dataHora,
     * barbeariaNome,
     * motivoCancelamento);
     * break;
     * default:
     * System.err.println("Tipo de evento desconhecido: " + tipoEvento);
     * }
     * 
     * } catch (Exception e) {
     * System.err.println("Erro ao preparar notificação de evento específico: " +
     * e.getMessage());
     * }
     * }
     * 
     * /**
     * Método auxiliar para notificar a criação de um agendamento.
     * 
     * @param agendamento Agendamento JPA salvo
     * @param clienteId   ID do cliente
     * @param servicoId   ID do serviço
     * @param funcionario Funcionário JPA
     */
    private void notificarCriacaoAgendamento(JpaAgendamento agendamento, Long clienteId, Long servicoId,
            JpaFuncionario funcionario) {
        try {
            // Buscar dados do cliente
            @SuppressWarnings("null")
            JpaCliente cliente = clienteRepository.findById(clienteId).orElse(null);
            if (cliente == null) {
                System.err.println("Cliente não encontrado para agendamento: " + agendamento.getId());
                return;
            }

            // Buscar dados do serviço
            @SuppressWarnings("null")
            JpaServico servico = servicoRepository.findById(servicoId).orElse(null);
            if (servico == null) {
                System.err.println("Serviço não encontrado para agendamento: " + agendamento.getId());
                return;
            }

            // Nome da barbearia (placeholder por enquanto)
            String barbeariaNome = "Sua Barbearia";

            // Formatar data/hora
            String dataHora = agendamento.getDataHora()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

            // Notificar criação
            notificarAgendamentoCriado(
                    agendamento.getId(),
                    cliente.getNome(),
                    cliente.getTelefone(),
                    servico.getNome(),
                    dataHora,
                    barbeariaNome);

        } catch (Exception e) {
            System.err.println("Erro ao preparar notificação de criação de agendamento: " + e.getMessage());
        }
    }

    /**
     * Notifica todos os event observers sobre a criação de um agendamento.
     * 
     * @param agendamentoId   ID do agendamento criado
     * @param clienteNome     Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome     Nome do serviço
     * @param dataHora        Data e hora do agendamento
     * @param barbeariaNome   Nome da barbearia
     */
    private void notificarAgendamentoCriado(
            Long agendamentoId,
            String clienteNome,
            String clienteTelefone,
            String servicoNome,
            String dataHora,
            String barbeariaNome) {

        for (AgendamentoEventObserver observer : eventObservers) {
            try {
                observer.onAgendamentoCriado(
                        agendamentoId,
                        clienteNome,
                        clienteTelefone,
                        servicoNome,
                        dataHora,
                        barbeariaNome);
            } catch (Exception e) {
                // Log erro mas não interrompe o fluxo
                System.err.println("Erro ao notificar event observer sobre criação: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica todos os event observers sobre o reagendamento de um agendamento.
     * 
     * @param agendamentoId   ID do agendamento reagendado
     * @param clienteNome     Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome     Nome do serviço
     * @param dataHoraAntiga  Data e hora antiga
     * @param dataHoraNova    Data e hora nova
     * @param barbeariaNome   Nome da barbearia
     */
    @SuppressWarnings("unused")
    private void notificarAgendamentoReagendado(
            Long agendamentoId,
            String clienteNome,
            String clienteTelefone,
            String servicoNome,
            String dataHoraAntiga,
            String dataHoraNova,
            String barbeariaNome) {

        for (AgendamentoEventObserver observer : eventObservers) {
            try {
                observer.onAgendamentoReagendado(
                        agendamentoId,
                        clienteNome,
                        clienteTelefone,
                        servicoNome,
                        dataHoraAntiga,
                        dataHoraNova,
                        barbeariaNome);
            } catch (Exception e) {
                // Log erro mas não interrompe o fluxo
                System.err.println("Erro ao notificar event observer sobre reagendamento: " + e.getMessage());
            }
        }
    }

    /**
     * Converte JpaAgendamento para AgendamentoBarbeariaDto com dados relacionados.
     * 
     * @param agendamento Entidade JPA do agendamento
     * @return DTO detalhado com informações de cliente, serviço e funcionário
     */
    @SuppressWarnings("null")
    private AgendamentoBarbeariaDto converterParaBarbeariaDto(JpaAgendamento agendamento) {
        // Buscar dados do cliente
        JpaCliente cliente = clienteRepository.findById(agendamento.getClienteId())
                .orElse(null);

        // Buscar dados do serviço
        JpaServico servico = servicoRepository.findById(agendamento.getServicoId())
                .orElse(null);

        // Buscar dados do funcionário
        JpaFuncionario funcionario = funcionarioRepository.findById(agendamento.getBarbeiroId())
                .orElse(null);

        return new AgendamentoBarbeariaDto(
                agendamento.getId(),
                agendamento.getDataHora(),
                agendamento.getStatus(),
                agendamento.getObservacoes(),

                // Dados do cliente
                cliente != null ? cliente.getId() : null,
                cliente != null ? cliente.getNome() : "Cliente não encontrado",
                cliente != null ? cliente.getTelefone() : "",

                // Dados do serviço
                servico != null ? servico.getId() : null,
                servico != null ? servico.getNome() : "Serviço não encontrado",
                servico != null ? servico.getTipoServico() : "GENERICO",
                servico != null ? servico.getPreco().doubleValue() : 0.0,
                servico != null ? servico.getDuracao() : 0,

                // Dados do funcionário
                funcionario != null ? funcionario.getId() : null,
                funcionario != null ? funcionario.getNome() : "Funcionário não encontrado",
                funcionario != null ? funcionario.getPerfilType().name() : "BARBEIRO",

                // Metadados
                agendamento.getDataCriacao(),
                agendamento.getDataAtualizacao());
    }

    /**
     * Cancela um agendamento.
     * 
     * @param agendamentoId ID do agendamento
     * @param usuarioId     ID do usuário solicitante
     * @param tipoUsuario   Tipo do usuário solicitante
     */
    @Transactional
    @SuppressWarnings("null")
    public void cancelarAgendamento(Long agendamentoId, Long usuarioId, String tipoUsuario) {
        // Busca com validação de permissão
        buscarAgendamentoPorId(agendamentoId, usuarioId, tipoUsuario);

        JpaAgendamento agendamento = agendamentoRepository.findById(agendamentoId).orElseThrow();

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new IllegalArgumentException("Agendamento já está cancelado");
        }

        if (agendamento.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível cancelar agendamentos passados");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamento.setDataAtualizacao(LocalDateTime.now());
        agendamentoRepository.save(agendamento);
    }

    /**
     * Reagenda um agendamento.
     * 
     * @param agendamentoId ID do agendamento
     * @param novaDataHora  Nova data e hora
     * @param usuarioId     ID do usuário solicitante
     * @param tipoUsuario   Tipo do usuário solicitante
     * @return Agendamento atualizado
     */
    @Transactional
    @SuppressWarnings("null")
    public AgendamentoResponseDto reagendarAgendamento(Long agendamentoId, LocalDateTime novaDataHora, Long usuarioId,
            String tipoUsuario) {
        // Busca com validação de permissão
        buscarAgendamentoPorId(agendamentoId, usuarioId, tipoUsuario);

        JpaAgendamento agendamento = agendamentoRepository.findById(agendamentoId).orElseThrow();

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new IllegalArgumentException("Não é possível reagendar um agendamento cancelado");
        }

        if (novaDataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nova data/hora não pode ser no passado");
        }

        // Verificar conflito de horário (excluindo o próprio agendamento se for o mesmo
        // horário, mas aqui mudou o horário)
        if (agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(
                agendamento.getBarbeiroId(),
                novaDataHora)) {
            throw new IllegalArgumentException("Horário não disponível para este funcionário");
        }

        agendamento.setDataHora(novaDataHora);
        agendamento.setDataAtualizacao(LocalDateTime.now());
        // Se estava confirmado, talvez devesse voltar para pendente?
        // Por simplicidade, mantemos o status, mas em um sistema real talvez precisasse
        // de reconfirmação.
        // O usuário pediu "Reagendar e cancelar", assumindo fluxo simples.

        JpaAgendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return AgendamentoMapper.toResponseDto(agendamentoSalvo);
    }

    /**
     * Confirma um agendamento.
     * 
     * @param agendamentoId ID do agendamento
     * @param usuarioId     ID do usuário autenticado
     * @param tipoUsuario   Tipo do usuário
     */
    @Transactional
    public void confirmarAgendamento(Long agendamentoId, Long usuarioId, String tipoUsuario) {
        if (agendamentoId == null)
            throw new IllegalArgumentException("ID do agendamento não pode ser nulo");

        JpaAgendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        if (!verificarAutorizacaoAcesso(agendamento, usuarioId, tipoUsuario)) {
            throw new AcessoNegadoException("Sem permissão para confirmar este agendamento");
        }

        if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
            throw new AcessoNegadoException("Clientes não podem confirmar agendamentos");
        }

        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento.setDataAtualizacao(LocalDateTime.now());
        agendamentoRepository.save(agendamento);
    }

    /**
     * Conclui um agendamento.
     * 
     * @param agendamentoId ID do agendamento
     * @param usuarioId     ID do usuário autenticado
     * @param tipoUsuario   Tipo do usuário
     */
    @Transactional
    public void concluirAgendamento(Long agendamentoId, Long usuarioId, String tipoUsuario) {
        if (agendamentoId == null)
            throw new IllegalArgumentException("ID do agendamento não pode ser nulo");

        JpaAgendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        if (!verificarAutorizacaoAcesso(agendamento, usuarioId, tipoUsuario)) {
            throw new AcessoNegadoException("Sem permissão para concluir este agendamento");
        }

        if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
            throw new AcessoNegadoException("Clientes não podem concluir agendamentos");
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamento.setDataAtualizacao(LocalDateTime.now());
        agendamentoRepository.save(agendamento);
    }

    /**
     * Busca um agendamento específico por ID para a barbearia.
     * Valida que o agendamento pertence à barbearia informada.
     * 
     * @param barbeariaId   ID da barbearia autenticada
     * @param agendamentoId ID do agendamento
     * @return DTO do agendamento
     * @throws IllegalArgumentException se agendamento não encontrado ou não
     *                                  pertence à barbearia
     */
    @SuppressWarnings("null")
    public AgendamentoBarbeariaDto buscarAgendamentoPorIdParaBarbearia(Long barbeariaId, Long agendamentoId) {
        JpaAgendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));

        // Verifica se o agendamento pertence à barbearia
        if (!agendamento.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Agendamento não encontrado ou não pertence a esta barbearia");
        }

        return converterParaBarbeariaDto(agendamento);
    }
}

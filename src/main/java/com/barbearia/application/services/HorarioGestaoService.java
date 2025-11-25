package com.barbearia.application.services;

import com.barbearia.application.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gestão de horários de funcionamento dos profissionais.
 * Responsável por CRUD completo e validações de regras de negócio.
 */
@Service
public class HorarioGestaoService {

    private final com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository horarioFuncionamentoRepository;
    private final com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository funcionarioRepository;
    private final com.barbearia.infrastructure.persistence.repositories.HorarioExcecaoRepository horarioExcecaoRepository;

    public HorarioGestaoService(
            com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository funcionarioRepository,
            com.barbearia.infrastructure.persistence.repositories.HorarioExcecaoRepository horarioExcecaoRepository) {
        this.horarioFuncionamentoRepository = horarioFuncionamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.horarioExcecaoRepository = horarioExcecaoRepository;
    }

    // ===== MÉTODOS PARA HORÁRIOS DE FUNCIONAMENTO =====

    /**
     * Lista os horários de funcionamento de um funcionário.
     */
    public List<HorarioFuncionamentoResponseDto> listarHorariosFuncionario(Long funcionarioId) {
        List<com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento> horarios = horarioFuncionamentoRepository
                .findByFuncionarioIdAtivo(funcionarioId);

        return horarios.stream()
                .map(h -> new HorarioFuncionamentoResponseDto(
                        h.getId(),
                        h.getBarbeariaId(),
                        h.getFuncionarioId(),
                        h.getDiaSemana(),
                        h.getHoraAbertura(),
                        h.getHoraFechamento(),
                        h.isAtivo()))
                .collect(Collectors.toList());
    }

    /**
     * Atualiza ou cria o horário de funcionamento de um funcionário para um dia
     * específico.
     */
    @Transactional
    public HorarioFuncionamentoResponseDto salvarHorarioFuncionario(Long barbeariaId, Long funcionarioId,
            HorarioFuncionamentoRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de horário não podem ser nulos");
        }

        if (!dto.isValid()) {
            throw new IllegalArgumentException("Dados de horário inválidos");
        }

        if (funcionarioId == null) {
            throw new IllegalArgumentException("ID do funcionário não pode ser nulo");
        }

        // Valida se o funcionário pertence à barbearia
        com.barbearia.infrastructure.persistence.entities.JpaFuncionario funcionario = funcionarioRepository
                .findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        // Verifica se já existe horário para este dia e funcionário (ativo ou inativo)
        com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento horario = horarioFuncionamentoRepository
                .findByFuncionarioIdAndDiaSemana(funcionarioId, dto.getDiaSemana())
                .orElse(new com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento(
                        barbeariaId, funcionarioId, dto.getDiaSemana(), dto.getHoraAbertura(),
                        dto.getHoraFechamento()));

        // Atualiza dados
        horario.setHoraAbertura(dto.getHoraAbertura());
        horario.setHoraFechamento(dto.getHoraFechamento());
        horario.setBarbeariaId(barbeariaId); // Garante que está vinculado à barbearia correta
        horario.setFuncionarioId(funcionarioId);
        // Se ativo não for informado, assume true (ativo)
        horario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        horario.setDataAtualizacao(java.time.LocalDateTime.now());

        com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento salvo = horarioFuncionamentoRepository
                .save(horario);

        return new HorarioFuncionamentoResponseDto(
                salvo.getId(),
                salvo.getBarbeariaId(),
                salvo.getFuncionarioId(),
                salvo.getDiaSemana(),
                salvo.getHoraAbertura(),
                salvo.getHoraFechamento(),
                salvo.isAtivo());
    }

    @Transactional
    public List<HorarioFuncionamentoResponseDto> salvarHorariosEmLote(Long barbeariaId, Long funcionarioId,
            HorarioLoteRequestDto dto) {
        return dto.getHorarios().stream()
                .map(horarioDto -> salvarHorarioFuncionario(barbeariaId, funcionarioId, horarioDto))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS PARA EXCEÇÕES DE HORÁRIO =====

    /**
     * Cria exceção de horário (disponibilidade extra em data específica).
     */
    @Transactional
    @SuppressWarnings("null")
    public HorarioExcecaoResponseDto criarExcecao(Long funcionarioId, HorarioExcecaoRequestDto dto, String criadoPor) {
        // Validações
        if (dto == null) {
            throw new IllegalArgumentException("Dados da exceção não podem ser nulos");
        }

        if (dto.getData() == null || dto.getHoraAbertura() == null || dto.getHoraFechamento() == null) {
            throw new IllegalArgumentException("Data, hora de abertura e fechamento são obrigatórias");
        }

        if (!dto.getHoraAbertura().isBefore(dto.getHoraFechamento())) {
            throw new IllegalArgumentException("Hora de abertura deve ser antes da hora de fechamento");
        }

        // Verifica se profissional existe
        com.barbearia.infrastructure.persistence.entities.JpaFuncionario funcionario = funcionarioRepository
                .findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        // Verifica se já existe exceção para esta data
        boolean jaExiste = horarioExcecaoRepository.existsByFuncionarioIdAndData(funcionarioId, dto.getData());
        if (jaExiste) {
            throw new IllegalArgumentException(
                    "Já existe exceção cadastrada para esta data. Remove a antiga primeiro.");
        }

        // Cria exceção
        com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao excecao = new com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao(
                funcionarioId,
                dto.getData(),
                dto.getHoraAbertura(),
                dto.getHoraFechamento(),
                dto.getMotivo(),
                criadoPor);

        com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao salva = horarioExcecaoRepository
                .save(excecao);

        return toResponseDto(salva, funcionario.getNome());
    }

    @Transactional
    public List<HorarioExcecaoResponseDto> criarExcecoesEmLote(Long funcionarioId, HorarioExcecaoLoteRequestDto dto,
            String criadoPor) {
        return dto.getExcecoes().stream()
                .map(excecaoDto -> criarExcecao(funcionarioId, excecaoDto, criadoPor))
                .collect(Collectors.toList());
    }

    /**
     * Lista exceções de um profissional em um período.
     */
    public List<HorarioExcecaoResponseDto> listarExcecoesPorPeriodo(
            Long funcionarioId,
            java.time.LocalDate dataInicio,
            java.time.LocalDate dataFim) {

        if (funcionarioId == null) {
            throw new IllegalArgumentException("ID do funcionário não pode ser nulo");
        }

        com.barbearia.infrastructure.persistence.entities.JpaFuncionario funcionario = funcionarioRepository
                .findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        List<com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao> excecoes = horarioExcecaoRepository
                .findByFuncionarioIdAndPeriodo(funcionarioId, dataInicio, dataFim);

        return excecoes.stream()
                .map(e -> toResponseDto(e, funcionario.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Remove exceção (apenas se criada pelo mesmo tipo de usuário).
     */
    @Transactional
    public void removerExcecao(Long excecaoId, Long funcionarioId, String removidoPor) {
        if (excecaoId == null) {
            throw new IllegalArgumentException("ID da exceção não pode ser nulo");
        }

        com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao excecao = horarioExcecaoRepository
                .findById(excecaoId)
                .orElseThrow(() -> new IllegalArgumentException("Exceção não encontrada"));

        // Verifica se a exceção pertence ao funcionário
        if (!excecao.getFuncionarioId().equals(funcionarioId)) {
            throw new IllegalArgumentException("Esta exceção não pertence a este profissional");
        }

        // Verifica permissão: profissional só pode remover as que ele criou
        if ("PROFISSIONAL".equals(removidoPor) && !"PROFISSIONAL".equals(excecao.getCriadoPor())) {
            throw new IllegalArgumentException("Você só pode remover exceções criadas por você");
        }

        horarioExcecaoRepository.delete(excecao);
    }

    /**
     * Busca exceção para data específica.
     */
    public java.util.Optional<com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao> buscarExcecaoPorData(
            Long funcionarioId, java.time.LocalDate data) {
        return horarioExcecaoRepository.findByFuncionarioIdAndData(funcionarioId, data);
    }

    // Helper para converter entidade em DTO
    private HorarioExcecaoResponseDto toResponseDto(
            com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao excecao,
            String funcionarioNome) {
        return new HorarioExcecaoResponseDto(
                excecao.getId(),
                excecao.getFuncionarioId(),
                funcionarioNome,
                excecao.getData(),
                excecao.getHoraAbertura(),
                excecao.getHoraFechamento(),
                excecao.getMotivo(),
                excecao.getCriadoPor(),
                excecao.getAtivo(),
                excecao.getDataCriacao());
    }
}

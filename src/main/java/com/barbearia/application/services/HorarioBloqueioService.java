package com.barbearia.application.services;

import com.barbearia.application.dto.*;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioBloqueado;
import com.barbearia.infrastructure.persistence.repositories.HorarioBloqueadoRepository;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Bloqueios de Horários.
 * 
 * Responsabilidades:
 * - CRUD de bloqueios de slots
 * - Validação de sobreposição
 * - Controle de permissões (quem criou)
 */
@Service
@Transactional
public class HorarioBloqueioService {

    private final HorarioBloqueadoRepository horarioBloqueadoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public HorarioBloqueioService(HorarioBloqueadoRepository horarioBloqueadoRepository,
            FuncionarioRepository funcionarioRepository) {
        this.horarioBloqueadoRepository = horarioBloqueadoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Cria bloqueio de horário.
     * 
     * @param funcionarioId ID do profissional
     * @param requestDto    Dados do bloqueio
     * @param criadoPor     "BARBEARIA" ou "PROFISSIONAL"
     * @return Bloqueio criado
     */
    @SuppressWarnings("null")
    public HorarioBloqueadoResponseDto criarBloqueio(Long funcionarioId,
            HorarioBloqueadoRequestDto requestDto,
            String criadoPor) {
        // Valida funcionário existe
        var funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        // Valida horários
        if (!requestDto.getHorarioInicio().isBefore(requestDto.getHorarioFim())) {
            throw new IllegalArgumentException("Horário de início deve ser antes do horário de fim");
        }

        // Verifica sobreposição
        boolean temSobreposicao = horarioBloqueadoRepository.existsSobreposicao(
                funcionarioId,
                requestDto.getData(),
                requestDto.getHorarioInicio(),
                requestDto.getHorarioFim());

        if (temSobreposicao) {
            throw new IllegalArgumentException("Já existe bloqueio neste horário");
        }

        JpaHorarioBloqueado bloqueio = new JpaHorarioBloqueado(
                funcionarioId,
                requestDto.getData(),
                requestDto.getHorarioInicio(),
                requestDto.getHorarioFim(),
                requestDto.getMotivo(),
                criadoPor);

        JpaHorarioBloqueado salvo = horarioBloqueadoRepository.save(bloqueio);

        return toResponseDto(salvo, funcionario.getNome());
    }

    /**
     * Cria múltiplos bloqueios em lote.
     * 
     * @param funcionarioId ID do profissional
     * @param requestDto    Lista de bloqueios
     * @param criadoPor     "BARBEARIA" ou "PROFISSIONAL"
     * @return Lista de bloqueios criados
     */
    public List<HorarioBloqueadoResponseDto> criarBloqueiosEmLote(
            Long funcionarioId,
            HorarioBloqueadoLoteRequestDto requestDto,
            String criadoPor) {

        return requestDto.getBloqueios().stream()
                .map(bloqueioDto -> criarBloqueio(funcionarioId, bloqueioDto, criadoPor))
                .collect(Collectors.toList());
    }

    /**
     * Lista bloqueios de um profissional em um período.
     */
    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public List<HorarioBloqueadoResponseDto> listarBloqueiosPorPeriodo(
            Long funcionarioId,
            LocalDate dataInicio,
            LocalDate dataFim) {

        var funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        return horarioBloqueadoRepository
                .findByFuncionarioIdAndPeriodo(funcionarioId, dataInicio, dataFim)
                .stream()
                .map(b -> toResponseDto(b, funcionario.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Remove bloqueio.
     * 
     * Validações:
     * - Profissional só pode remover bloqueios criados por ele
     * - Barbearia pode remover qualquer bloqueio
     */
    @SuppressWarnings("null")
    public void removerBloqueio(Long bloqueioId, Long funcionarioId, String removidoPor) {
        JpaHorarioBloqueado bloqueio = horarioBloqueadoRepository.findById(bloqueioId)
                .orElseThrow(() -> new IllegalArgumentException("Bloqueio não encontrado"));

        if (!bloqueio.getFuncionarioId().equals(funcionarioId)) {
            throw new IllegalArgumentException("Bloqueio não pertence a este profissional");
        }

        // Profissional só pode remover bloqueios criados por ele
        if ("PROFISSIONAL".equals(removidoPor) && !"PROFISSIONAL".equals(bloqueio.getCriadoPor())) {
            throw new IllegalArgumentException("Profissional só pode remover bloqueios criados por ele");
        }

        horarioBloqueadoRepository.delete(bloqueio);
    }

    /**
     * Lista bloqueios de uma data específica (usado para cálculo de
     * disponibilidade).
     */
    @Transactional(readOnly = true)
    public List<JpaHorarioBloqueado> listarBloqueiosPorData(Long funcionarioId, LocalDate data) {
        return horarioBloqueadoRepository.findByFuncionarioIdAndData(funcionarioId, data);
    }

    // Helper methods

    private HorarioBloqueadoResponseDto toResponseDto(JpaHorarioBloqueado bloqueio, String funcionarioNome) {
        return new HorarioBloqueadoResponseDto(
                bloqueio.getId(),
                bloqueio.getFuncionarioId(),
                funcionarioNome,
                bloqueio.getData(),
                bloqueio.getHorarioInicio(),
                bloqueio.getHorarioFim(),
                bloqueio.getMotivo(),
                bloqueio.getCriadoPor(),
                bloqueio.getDataCriacao());
    }
}

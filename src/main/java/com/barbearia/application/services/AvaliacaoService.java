package com.barbearia.application.services;

import com.barbearia.application.dto.*;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.infrastructure.persistence.entities.*;
import com.barbearia.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Avaliações de Barbearias.
 * 
 * Responsabilidades:
 * - CRUD completo de avaliações
 * - Cálculo de médias e estatísticas
 * - Validação de regras de negócio
 */
@Service
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
            AgendamentoRepository agendamentoRepository,
            ClienteRepository clienteRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Cria nova avaliação multi-aspecto.
     * 
     * Validações:
     * - Cliente deve ter agendamento CONCLUÍDO
     * - Uma avaliação por agendamento
     * - Todas as 4 notas obrigatórias
     */
    @SuppressWarnings("null")
    public AvaliacaoResponseDto criarAvaliacao(Long clienteId, AvaliacaoRequestDto requestDto) {
        // Valida se agendamento existe e pertence ao cliente
        var agendamento = agendamentoRepository.findById(requestDto.getAgendamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));

        if (!agendamento.getClienteId().equals(clienteId)) {
            throw new IllegalArgumentException("Agendamento não pertence ao cliente");
        }

        // Valida se agendamento está concluído
        if (agendamento.getStatus() != StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException("Apenas agendamentos concluídos podem ser avaliados");
        }

        // Valida se já existe avaliação para este agendamento
        if (avaliacaoRepository.existsByAgendamentoId(requestDto.getAgendamentoId())) {
            throw new IllegalArgumentException("Agendamento já foi avaliado");
        }

        // Cria avaliação
        JpaAvaliacao avaliacao = new JpaAvaliacao(
                clienteId,
                requestDto.getBarbeariaId(),
                requestDto.getAgendamentoId(),
                requestDto.getNotaServico(),
                requestDto.getNotaAmbiente(),
                requestDto.getNotaLimpeza(),
                requestDto.getNotaAtendimento(),
                requestDto.getComentario());

        // @PrePersist calcula nota_geral automaticamente
        JpaAvaliacao salva = avaliacaoRepository.save(avaliacao);

        // Busca nome do cliente
        var cliente = clienteRepository.findById(clienteId);
        String clienteNome = cliente.map(c -> c.getNome()).orElse("Anônimo");

        return toResponseDto(salva, clienteNome);
    }

    /**
     * Lista avaliações de uma barbearia (ordenadas por data, mais recentes
     * primeiro).
     */
    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDto> buscarAvaliacoesPorBarbearia(Long barbeariaId) {
        return avaliacaoRepository.findByBarbeariaIdOrderByDataCriacaoDesc(barbeariaId)
                .stream()
                .map(av -> {
                    String nome = clienteRepository.findById(av.getClienteId())
                            .map(c -> c.getNome())
                            .orElse("Anônimo");
                    return toResponseDto(av, nome);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula estatísticas completas de avaliações.
     */
    @Transactional(readOnly = true)
    public EstatisticasAvaliacoesDto calcularEstatisticas(Long barbeariaId) {
        Double mediaGeral = avaliacaoRepository.calcularMediaGeral(barbeariaId);
        Double mediaServico = avaliacaoRepository.calcularMediaServico(barbeariaId);
        Double mediaAmbiente = avaliacaoRepository.calcularMediaAmbiente(barbeariaId);
        Double mediaLimpeza = avaliacaoRepository.calcularMediaLimpeza(barbeariaId);
        Double mediaAtendimento = avaliacaoRepository.calcularMediaAtendimento(barbeariaId);
        long total = avaliacaoRepository.countByBarbeariaId(barbeariaId);

        EstatisticasAvaliacoesDto stats = new EstatisticasAvaliacoesDto(
                barbeariaId,
                mediaGeral != null ? mediaGeral : 0.0,
                mediaServico != null ? mediaServico : 0.0,
                mediaAmbiente != null ? mediaAmbiente : 0.0,
                mediaLimpeza != null ? mediaLimpeza : 0.0,
                mediaAtendimento != null ? mediaAtendimento : 0.0,
                total);

        // Distribui\u00e7\u00e3o de notas (1-5 estrelas)
        stats.setAvaliacoes1Estrela(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbeariaId, 1));
        stats.setAvaliacoes2Estrelas(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbeariaId, 2));
        stats.setAvaliacoes3Estrelas(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbeariaId, 3));
        stats.setAvaliacoes4Estrelas(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbeariaId, 4));
        stats.setAvaliacoes5Estrelas(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbeariaId, 5));

        return stats;
    }

    // Helper methods

    private AvaliacaoResponseDto toResponseDto(JpaAvaliacao avaliacao, String clienteNome) {
        return new AvaliacaoResponseDto(
                avaliacao.getId(),
                avaliacao.getBarbeariaId(),
                avaliacao.getAgendamentoId(),
                clienteNome,
                avaliacao.getNotaServico(),
                avaliacao.getNotaAmbiente(),
                avaliacao.getNotaLimpeza(),
                avaliacao.getNotaAtendimento(),
                avaliacao.getNotaGeral(),
                avaliacao.getComentario(),
                avaliacao.getDataCriacao());
    }
}

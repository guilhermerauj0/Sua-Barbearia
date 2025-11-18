package com.barbearia.application.services;

import com.barbearia.application.dto.ComissaoFuncionarioDto;
import com.barbearia.application.dto.RelatorioComissoesDto;
import com.barbearia.domain.entities.*;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.infrastructure.persistence.entities.*;
import com.barbearia.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para cálculo e geração de relatórios de comissões.
 * 
 * Calcula comissões baseadas no perfil do funcionário:
 * - BARBEIRO: 15%
 * - MANICURE: 12%
 * - ESTETICISTA: 13%
 * - COLORISTA: 18%
 * 
 * @author Sua Barbearia Team
 */
@Service
public class ComissaoService {
    
    private final AgendamentoRepository agendamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ServicoRepository servicoRepository;
    private final BarbeariaRepository barbeariaRepository;
    
    public ComissaoService(
            AgendamentoRepository agendamentoRepository,
            FuncionarioRepository funcionarioRepository,
            ServicoRepository servicoRepository,
            BarbeariaRepository barbeariaRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.servicoRepository = servicoRepository;
        this.barbeariaRepository = barbeariaRepository;
    }
    
    /**
     * Gera relatório de comissões para um período específico.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Relatório completo de comissões
     */
    @Transactional(readOnly = true)
    public RelatorioComissoesDto gerarRelatorioComissoes(
            Long barbeariaId, 
            LocalDate dataInicio, 
            LocalDate dataFim) {
        
        // Validar barbeariaId
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        
        // Buscar barbearia
        JpaBarbearia barbearia = barbeariaRepository.findById(barbeariaId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));
        
        // Converter datas para LocalDateTime
        LocalDateTime dataHoraInicio = dataInicio.atStartOfDay();
        LocalDateTime dataHoraFim = dataFim.atTime(23, 59, 59);
        
        // Buscar agendamentos concluídos no período
        List<JpaAgendamento> agendamentos = agendamentoRepository
                .findByBarbeariaIdAndDataHoraBetweenAndStatus(
                        barbeariaId, 
                        dataHoraInicio, 
                        dataHoraFim,
                        StatusAgendamento.CONCLUIDO
                );
        
        // Agrupar agendamentos por funcionário
        Map<Long, List<JpaAgendamento>> agendamentosPorFuncionario = agendamentos.stream()
                .filter(a -> a.getBarbeiroId() != null)
                .collect(Collectors.groupingBy(JpaAgendamento::getBarbeiroId));
        
        // Calcular comissões por funcionário
        List<ComissaoFuncionarioDto> comissoesPorFuncionario = new ArrayList<>();
        BigDecimal totalComissoesGeral = BigDecimal.ZERO;
        BigDecimal valorTotalServicosGeral = BigDecimal.ZERO;
        
        for (Map.Entry<Long, List<JpaAgendamento>> entry : agendamentosPorFuncionario.entrySet()) {
            Long funcionarioId = entry.getKey();
            List<JpaAgendamento> agendamentosFuncionario = entry.getValue();
            
            // Validar funcionarioId
            if (funcionarioId == null) {
                continue;
            }
            
            // Buscar funcionário
            JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                    .orElse(null);
            
            if (funcionario == null || !funcionario.isAtivo()) {
                continue;
            }
            
            // Criar Funcionario de domínio para acessar perfil
            Funcionario funcionarioDomain = mapearParaDomain(funcionario);
            
            // Calcular totais para este funcionário
            BigDecimal valorTotalServicos = BigDecimal.ZERO;
            BigDecimal totalComissoes = BigDecimal.ZERO;
            int totalServicos = agendamentosFuncionario.size();
            
            for (JpaAgendamento agendamento : agendamentosFuncionario) {
                // Validar servicoId
                Long servicoId = agendamento.getServicoId();
                if (servicoId == null) {
                    continue;
                }
                
                // Buscar serviço para obter valor
                JpaServico servico = servicoRepository.findById(servicoId)
                        .orElse(null);
                
                if (servico != null && servico.getPreco() != null) {
                    BigDecimal valorServico = servico.getPreco();
                    valorTotalServicos = valorTotalServicos.add(valorServico);
                    
                    // Calcular comissão usando o perfil do funcionário
                    double comissao = funcionarioDomain.calcularComissao(valorServico.doubleValue());
                    totalComissoes = totalComissoes.add(BigDecimal.valueOf(comissao));
                }
            }
            
            // Obter taxa de comissão
            double taxaComissao = obterTaxaComissao(funcionarioDomain);
            
            // Criar DTO
            ComissaoFuncionarioDto comissaoDto = new ComissaoFuncionarioDto(
                    funcionario.getId(),
                    funcionario.getNome(),
                    funcionario.getEmail(),
                    funcionario.getPerfilType(),
                    funcionarioDomain.getProfissao(),
                    taxaComissao,
                    totalServicos,
                    valorTotalServicos,
                    totalComissoes
            );
            
            comissoesPorFuncionario.add(comissaoDto);
            totalComissoesGeral = totalComissoesGeral.add(totalComissoes);
            valorTotalServicosGeral = valorTotalServicosGeral.add(valorTotalServicos);
        }
        
        // Ordenar por total de comissões (decrescente)
        comissoesPorFuncionario.sort((a, b) -> 
            b.totalComissoes().compareTo(a.totalComissoes())
        );
        
        // Criar relatório completo
        return new RelatorioComissoesDto(
                barbeariaId,
                barbearia.getNome(),
                dataInicio,
                dataFim,
                comissoesPorFuncionario,
                agendamentos.size(),
                valorTotalServicosGeral,
                totalComissoesGeral
        );
    }
    
    /**
     * Mapeia JpaFuncionario para Funcionario de domínio.
     */
    private Funcionario mapearParaDomain(JpaFuncionario jpaFuncionario) {
        return new Funcionario(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo(),
                jpaFuncionario.getPerfilType()
        );
    }
    
    /**
     * Obtém taxa de comissão baseada no perfil.
     */
    private double obterTaxaComissao(Funcionario funcionario) {
        // Usar um valor padrão de 100 para calcular a taxa percentual
        double comissaoDe100 = funcionario.calcularComissao(100.0);
        return comissaoDe100; // Retorna a taxa em percentual (ex: 15.0 para 15%)
    }
}

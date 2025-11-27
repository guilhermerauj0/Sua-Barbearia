package com.barbearia.application.services;

import com.barbearia.application.dto.*;
import com.barbearia.domain.enums.CategoriaDespesa;
import com.barbearia.domain.enums.CategoriaReceitaExtra;
import com.barbearia.domain.enums.TipoTransacao;
import com.barbearia.infrastructure.persistence.entities.JpaTransacaoFinanceira;
import com.barbearia.infrastructure.persistence.repositories.TransacaoFinanceiraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gestão de transações financeiras (despesas e receitas extras).
 */
@Service
public class TransacaoFinanceiraService {

    private final TransacaoFinanceiraRepository repository;

    public TransacaoFinanceiraService(TransacaoFinanceiraRepository repository) {
        this.repository = repository;
    }

    // ==================== DESPESAS ====================

    @Transactional
    public TransacaoFinanceiraDto criarDespesa(Long barbeariaId, DespesaRequestDto dto) {
        JpaTransacaoFinanceira transacao = new JpaTransacaoFinanceira();
        transacao.setBarbeariaId(barbeariaId);
        transacao.setTipoTransacao(TipoTransacao.DESPESA);
        transacao.setValor(dto.valor());
        transacao.setCategoria(dto.categoria().name());
        transacao.setDescricao(dto.descricao());
        transacao.setDataTransacao(dto.dataTransacao());

        JpaTransacaoFinanceira salva = repository.save(transacao);
        return toDto(salva);
    }

    public ListaDespesasDto listarDespesas(
            Long barbeariaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            CategoriaDespesa categoria) {

        List<JpaTransacaoFinanceira> despesas;

        if (categoria != null) {
            despesas = repository.findByPeriodoTipoECategoria(
                    barbeariaId, dataInicio, dataFim, TipoTransacao.DESPESA, categoria.name());
        } else {
            despesas = repository.findByPeriodoETipo(
                    barbeariaId, dataInicio, dataFim, TipoTransacao.DESPESA);
        }

        BigDecimal total = despesas.stream()
                .map(JpaTransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoriaResumoDto> resumo = obterResumoPorCategoria(
                barbeariaId, dataInicio, dataFim, TipoTransacao.DESPESA);

        @SuppressWarnings("null")
        long quantidade = (long) despesas.size();

        return new ListaDespesasDto(
                total,
                quantidade,
                new PeriodoDto(dataInicio, dataFim),
                despesas.stream().map(this::toDto).collect(Collectors.toList()),
                resumo);
    }

    @Transactional
    public TransacaoFinanceiraDto atualizarDespesa(
            Long barbeariaId,
            Long despesaId,
            DespesaRequestDto dto) {

        @SuppressWarnings("null")
        JpaTransacaoFinanceira transacao = repository.findById(despesaId)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada"));

        if (!transacao.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Despesa não pertence a esta barbearia");
        }

        if (transacao.getTipoTransacao() != TipoTransacao.DESPESA) {
            throw new IllegalArgumentException("Transação não é uma despesa");
        }

        @SuppressWarnings("null")
        String categoriaName = dto.categoria().name();
        transacao.setValor(dto.valor());
        transacao.setCategoria(categoriaName);
        transacao.setDescricao(dto.descricao());
        transacao.setDataTransacao(dto.dataTransacao());

        JpaTransacaoFinanceira atualizada = repository.save(transacao);
        return toDto(atualizada);
    }

    @SuppressWarnings("null")
    @Transactional
    public void excluirDespesa(Long barbeariaId, Long despesaId) {
        boolean exists = repository.existsByIdAndBarbeariaId(despesaId, barbeariaId);
        if (!exists) {
            throw new IllegalArgumentException("Despesa não encontrada ou não pertence a esta barbearia");
        }

        repository.deleteById(despesaId);
    }

    // ==================== RECEITAS EXTRAS ====================

    @Transactional
    public TransacaoFinanceiraDto criarReceitaExtra(Long barbeariaId, ReceitaExtraRequestDto dto) {
        JpaTransacaoFinanceira transacao = new JpaTransacaoFinanceira();
        transacao.setBarbeariaId(barbeariaId);
        transacao.setTipoTransacao(TipoTransacao.RECEITA_EXTRA);
        transacao.setValor(dto.valor());
        transacao.setCategoria(dto.categoria().name());
        transacao.setDescricao(dto.descricao());
        transacao.setDataTransacao(dto.dataTransacao());

        JpaTransacaoFinanceira salva = repository.save(transacao);
        return toDto(salva);
    }

    public ListaReceitasExtrasDto listarReceitasExtras(
            Long barbeariaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            CategoriaReceitaExtra categoria) {

        List<JpaTransacaoFinanceira> receitas;

        if (categoria != null) {
            receitas = repository.findByPeriodoTipoECategoria(
                    barbeariaId, dataInicio, dataFim, TipoTransacao.RECEITA_EXTRA, categoria.name());
        } else {
            receitas = repository.findByPeriodoETipo(
                    barbeariaId, dataInicio, dataFim, TipoTransacao.RECEITA_EXTRA);
        }

        BigDecimal total = receitas.stream()
                .map(JpaTransacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoriaResumoDto> resumo = obterResumoPorCategoria(
                barbeariaId, dataInicio, dataFim, TipoTransacao.RECEITA_EXTRA);

        @SuppressWarnings("null")
        long quantidade = (long) receitas.size();

        return new ListaReceitasExtrasDto(
                total,
                quantidade,
                new PeriodoDto(dataInicio, dataFim),
                receitas.stream().map(this::toDto).collect(Collectors.toList()),
                resumo);
    }

    @Transactional
    public TransacaoFinanceiraDto atualizarReceitaExtra(
            Long barbeariaId,
            Long receitaId,
            ReceitaExtraRequestDto dto) {

        @SuppressWarnings("null")
        JpaTransacaoFinanceira transacao = repository.findById(receitaId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

        if (!transacao.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Receita não pertence a esta barbearia");
        }

        if (transacao.getTipoTransacao() != TipoTransacao.RECEITA_EXTRA) {
            throw new IllegalArgumentException("Transação não é uma receita extra");
        }

        @SuppressWarnings("null")
        String categoriaName = dto.categoria().name();
        transacao.setValor(dto.valor());
        transacao.setCategoria(categoriaName);
        transacao.setDescricao(dto.descricao());
        transacao.setDataTransacao(dto.dataTransacao());

        JpaTransacaoFinanceira atualizada = repository.save(transacao);
        return toDto(atualizada);
    }

    @SuppressWarnings("null")
    @Transactional
    public void excluirReceitaExtra(Long barbeariaId, Long receitaId) {
        boolean exists = repository.existsByIdAndBarbeariaId(receitaId, barbeariaId);
        if (!exists) {
            throw new IllegalArgumentException("Receita não encontrada ou não pertence a esta barbearia");
        }

        repository.deleteById(receitaId);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private List<CategoriaResumoDto> obterResumoPorCategoria(
            Long barbeariaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            TipoTransacao tipo) {

        List<Object[]> resultados = repository.obterResumoPorCategoria(
                barbeariaId, dataInicio, dataFim, tipo);

        BigDecimal totalGeral = resultados.stream()
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return resultados.stream()
                .map(r -> {
                    String categoria = (String) r[0];
                    BigDecimal total = (BigDecimal) r[1];
                    Long quantidade = (Long) r[2];

                    BigDecimal percentual = totalGeral.compareTo(BigDecimal.ZERO) > 0
                            ? total.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new CategoriaResumoDto(categoria, total, percentual, quantidade);
                })
                .collect(Collectors.toList());
    }

    private TransacaoFinanceiraDto toDto(JpaTransacaoFinanceira entity) {
        return new TransacaoFinanceiraDto(
                entity.getId(),
                entity.getBarbeariaId(),
                entity.getTipoTransacao(),
                entity.getValor(),
                entity.getCategoria(),
                entity.getDescricao(),
                entity.getDataTransacao(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao());
    }
}

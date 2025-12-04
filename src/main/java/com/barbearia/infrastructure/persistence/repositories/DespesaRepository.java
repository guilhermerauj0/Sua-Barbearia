package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaDespesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Despesa.
 */
@Repository
public interface DespesaRepository extends JpaRepository<JpaDespesa, Long> {

    /**
     * Busca despesas de uma barbearia em um intervalo de datas.
     */
    List<JpaDespesa> findByBarbeariaIdAndDataTransacaoBetweenOrderByDataTransacaoDesc(
            Long barbeariaId, LocalDate inicio, LocalDate fim);

    /**
     * Busca todas as despesas de uma barbearia.
     */
    List<JpaDespesa> findByBarbeariaIdOrderByDataTransacaoDesc(Long barbeariaId);
}

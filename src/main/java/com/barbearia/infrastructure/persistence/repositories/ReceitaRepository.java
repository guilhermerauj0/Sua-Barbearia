package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaReceita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Receita.
 */
@Repository
public interface ReceitaRepository extends JpaRepository<JpaReceita, Long> {

    /**
     * Busca receitas de uma barbearia em um intervalo de datas.
     */
    List<JpaReceita> findByBarbeariaIdAndDataTransacaoBetweenOrderByDataTransacaoDesc(
            Long barbeariaId, LocalDate inicio, LocalDate fim);

    /**
     * Busca todas as receitas de uma barbearia.
     */
    List<JpaReceita> findByBarbeariaIdOrderByDataTransacaoDesc(Long barbeariaId);
}

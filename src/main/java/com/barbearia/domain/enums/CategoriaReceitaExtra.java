package com.barbearia.domain.enums;

/**
 * Categorias de receitas extras (além dos agendamentos).
 */
public enum CategoriaReceitaExtra {
    VENDA_PRODUTO("Venda de Produtos"),
    COMISSAO_EXTERNA("Comissões e Parcerias"),
    ALUGUEL_ESPACO("Aluguel de Espaço/Cadeira"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaReceitaExtra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

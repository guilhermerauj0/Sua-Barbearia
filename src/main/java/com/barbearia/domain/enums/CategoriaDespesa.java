package com.barbearia.domain.enums;

/**
 * Categorias de despesas operacionais.
 */
public enum CategoriaDespesa {
    ALUGUEL("Aluguel"),
    SALARIO("Salários e Encargos"),
    PRODUTOS("Produtos e Materiais"),
    ENERGIA("Energia Elétrica"),
    AGUA("Água e Esgoto"),
    INTERNET("Internet e Telefone"),
    MANUTENCAO("Manutenção e Reparos"),
    MARKETING("Marketing e Publicidade"),
    IMPOSTOS("Impostos e Taxas"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaDespesa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

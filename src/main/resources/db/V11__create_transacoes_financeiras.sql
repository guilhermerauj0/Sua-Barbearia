-- Migration para sistema de gestão financeira
-- Cria tabela de transações financeiras (despesas e receitas extras)

CREATE TABLE transacoes_financeiras (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    tipo_transacao VARCHAR(20) NOT NULL CHECK (tipo_transacao IN ('DESPESA', 'RECEITA_EXTRA')),
    valor DECIMAL(10,2) NOT NULL CHECK (valor > 0),
    categoria VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    data_transacao DATE NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_transacao_barbearia 
        FOREIGN KEY (barbearia_id) 
        REFERENCES barbearias(id) 
        ON DELETE CASCADE
);

-- Índices para otimização de consultas
CREATE INDEX idx_transacao_barbearia ON transacoes_financeiras(barbearia_id);
CREATE INDEX idx_transacao_tipo ON transacoes_financeiras(tipo_transacao);
CREATE INDEX idx_transacao_data ON transacoes_financeiras(data_transacao);
CREATE INDEX idx_transacao_categoria ON transacoes_financeiras(categoria);
CREATE INDEX idx_transacao_barbearia_tipo_data ON transacoes_financeiras(barbearia_id, tipo_transacao, data_transacao);

-- Comentários para documentação
COMMENT ON TABLE transacoes_financeiras IS 'Tabela de controle financeiro (despesas e receitas extras)';
COMMENT ON COLUMN transacoes_financeiras.tipo_transacao IS 'Tipo: DESPESA ou RECEITA_EXTRA';
COMMENT ON COLUMN transacoes_financeiras.categoria IS 'Categoria específica baseada no tipo (ex: ALUGUEL, VENDA_PRODUTO)';
COMMENT ON COLUMN transacoes_financeiras.data_transacao IS 'Data em que a transação ocorreu';

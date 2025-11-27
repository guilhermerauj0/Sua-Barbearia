-- Create despesas table
CREATE TABLE despesas (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    data_transacao DATE NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_despesa_barbearia_data ON despesas(barbearia_id, data_transacao);

-- Create receitas_extras table
CREATE TABLE receitas_extras (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    data_transacao DATE NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_receita_barbearia_data ON receitas_extras(barbearia_id, data_transacao);

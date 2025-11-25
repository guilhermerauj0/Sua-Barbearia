-- Migration para adicionar tabela de exceções de horário
-- Permite profissionais e barbearias adicionarem disponibilidade extra em datas específicas

CREATE TABLE horarios_excecoes (
    id BIGSERIAL PRIMARY KEY,
    funcionario_id BIGINT NOT NULL,
    data DATE NOT NULL,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    motivo VARCHAR(255),
    criado_por VARCHAR(20) NOT NULL, -- 'BARBEARIA' ou 'PROFISSIONAL'
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_excecoes_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT uk_excecoes_funcionario_data UNIQUE (funcionario_id, data)
);

-- Índices
CREATE INDEX idx_excecoes_funcionario_data ON horarios_excecoes(funcionario_id, data);
CREATE INDEX idx_excecoes_data ON horarios_excecoes(data);

-- Comentários
COMMENT ON TABLE horarios_excecoes IS 'Exceções de horário - disponibilidade extra em datas específicas';
COMMENT ON COLUMN horarios_excecoes.criado_por IS 'Quem criou a exceção: BARBEARIA ou PROFISSIONAL';

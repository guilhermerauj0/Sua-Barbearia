-- ===========================================
-- Migration V5: Sistema de Avaliações Multi-aspecto
-- Sistema: Sua Barbearia
-- Adiciona tabela de avaliações de barbearias
-- ===========================================

-- Tabela de Avaliações
CREATE TABLE IF NOT EXISTS avaliacoes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    barbearia_id BIGINT NOT NULL,
    agendamento_id BIGINT,
    
    -- Notas individuais (1-5)
    nota_servico INTEGER NOT NULL CHECK (nota_servico BETWEEN 1 AND 5),
    nota_ambiente INTEGER NOT NULL CHECK (nota_ambiente BETWEEN 1 AND 5),
    nota_limpeza INTEGER NOT NULL CHECK (nota_limpeza BETWEEN 1 AND 5),
    nota_atendimento INTEGER NOT NULL CHECK (nota_atendimento BETWEEN 1 AND 5),
    
    -- Nota geral calculada (média das 4 notas)
    nota_geral DECIMAL(3,2) NOT NULL,
    
    -- Comentário opcional
    comentario TEXT,
    
    -- Timestamps
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_avaliacoes_cliente FOREIGN KEY (cliente_id) 
        REFERENCES clientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacoes_barbearia FOREIGN KEY (barbearia_id) 
        REFERENCES barbearias(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacoes_agendamento FOREIGN KEY (agendamento_id) 
        REFERENCES agendamentos(id) ON DELETE SET NULL,
    
    -- Uma avaliação por agendamento
    CONSTRAINT uk_avaliacoes_cliente_agendamento 
        UNIQUE (cliente_id, agendamento_id)
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_avaliacoes_barbearia_nota 
    ON avaliacoes(barbearia_id, nota_geral);

CREATE INDEX IF NOT EXISTS idx_avaliacoes_barbearia_data 
    ON avaliacoes(barbearia_id, data_criacao DESC);

CREATE INDEX IF NOT EXISTS idx_avaliacoes_cliente 
    ON avaliacoes(cliente_id);

-- Comentários de documentação
COMMENT ON TABLE avaliacoes IS 'Armazena avaliações multi-aspecto de barbearias feitas por clientes';
COMMENT ON COLUMN avaliacoes.nota_servico IS 'Avaliação da qualidade do serviço prestado (1-5)';
COMMENT ON COLUMN avaliacoes.nota_ambiente IS 'Avaliação do ambiente/local da barbearia (1-5)';
COMMENT ON COLUMN avaliacoes.nota_limpeza IS 'Avaliação da limpeza do estabelecimento (1-5)';
COMMENT ON COLUMN avaliacoes.nota_atendimento IS 'Avaliação da qualidade do atendimento (1-5)';
COMMENT ON COLUMN avaliacoes.nota_geral IS 'Média automática das 4 notas individuais';

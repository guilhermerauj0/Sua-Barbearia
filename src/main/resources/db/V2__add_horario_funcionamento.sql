-- ===========================================
-- Adicionar tabelas para T10 - Horários Disponíveis
-- Sistema: Sua Barbearia
-- PostgreSQL
-- ===========================================

-- Tabela de Horários de Funcionamento (por dia da semana)
CREATE TABLE IF NOT EXISTS horarios_funcionamento (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    dia_semana INTEGER NOT NULL,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_horarios_funcionamento_barbearia FOREIGN KEY (barbearia_id) REFERENCES barbearias(id) ON DELETE CASCADE,
    CONSTRAINT uq_barbearia_dia_semana UNIQUE (barbearia_id, dia_semana)
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_horarios_funcionamento_barbearia ON horarios_funcionamento(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_horarios_funcionamento_barbearia_ativo ON horarios_funcionamento(barbearia_id, ativo);
CREATE INDEX IF NOT EXISTS idx_horarios_funcionamento_barbearia_dia_ativo ON horarios_funcionamento(barbearia_id, dia_semana, ativo);

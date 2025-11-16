-- ===========================================
-- Adicionar coluna data_atualizacao à tabela de barbearias
-- ===========================================

ALTER TABLE barbearias
    ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Atualiza registros existentes para garantir consistência
UPDATE barbearias
SET data_atualizacao = COALESCE(data_atualizacao, data_criacao, CURRENT_TIMESTAMP);

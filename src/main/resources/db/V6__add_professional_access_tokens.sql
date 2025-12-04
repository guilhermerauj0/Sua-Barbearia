-- ===========================================
-- Migration V6: Sistema de Tokens de Acesso para Profissionais
-- Sistema: Sua Barbearia
-- Adiciona controle de links de acesso para profissionais
-- ===========================================

-- Adiciona colunas para sistema de tokens na tabela funcionarios
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS access_token VARCHAR(255) UNIQUE;
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS token_ativo BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS token_gerado_em TIMESTAMP;
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS token_expira_em TIMESTAMP;

-- Índice para busca rápida de tokens válidos
CREATE INDEX IF NOT EXISTS idx_funcionarios_access_token 
    ON funcionarios(access_token) 
    WHERE token_ativo = TRUE;

-- Comentários de documentação
COMMENT ON COLUMN funcionarios.access_token IS 'Token UUID único para acesso via link (sem senha)';
COMMENT ON COLUMN funcionarios.token_ativo IS 'Indica se o token está ativo (controlado pela barbearia)';
COMMENT ON COLUMN funcionarios.token_gerado_em IS 'Data/hora de geração do token';
COMMENT ON COLUMN funcionarios.token_expira_em IS 'Data/hora de expiração do token (NULL = sem expiração)';

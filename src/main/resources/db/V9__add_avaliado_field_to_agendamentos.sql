-- ===========================================
-- Migration V9: Campo Avaliado em Agendamentos
-- Sistema: Sua Barbearia
-- Adiciona campo para rastrear se agendamento já foi avaliado
-- ===========================================

-- Adicionar coluna avaliado à tabela agendamentos
ALTER TABLE agendamentos 
ADD COLUMN avaliado BOOLEAN NOT NULL DEFAULT FALSE;

-- Criar índice para otimizar consultas por agendamentos não avaliados
CREATE INDEX IF NOT EXISTS idx_agendamentos_avaliado 
    ON agendamentos(avaliado);

-- Criar índice composto para consultas de agendamentos concluídos não avaliados
CREATE INDEX IF NOT EXISTS idx_agendamentos_status_avaliado 
    ON agendamentos(status, avaliado);

-- Comentário de documentação
COMMENT ON COLUMN agendamentos.avaliado IS 'Indica se o agendamento já foi avaliado pelo cliente';

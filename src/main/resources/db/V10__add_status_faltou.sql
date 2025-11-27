-- ===========================================
-- Migration V10: Adicionar Status FALTOU
-- Sistema: Sua Barbearia
-- Atualiza constraint de status para incluir FALTOU
-- ===========================================

-- Remover constraint existente de status
ALTER TABLE agendamentos 
DROP CONSTRAINT IF EXISTS chk_status_agendamento;

-- Recriar constraint com novo status FALTOU
ALTER TABLE agendamentos 
ADD CONSTRAINT chk_status_agendamento 
CHECK (status IN ('PENDENTE', 'CONFIRMADO', 'CONCLUIDO', 'CANCELADO', 'FALTOU'));

-- Comentário de documentação
COMMENT ON COLUMN agendamentos.status IS 'Status do agendamento: PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO, FALTOU';

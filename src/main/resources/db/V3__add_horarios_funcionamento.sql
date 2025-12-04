-- ===========================================
-- Migration V3: Gestão de Horários de Funcionamento
-- Sistema: Sua Barbearia
-- Adiciona tabela para horários de funcionamento dos profissionais
-- ===========================================

-- Tabela de Horários de Funcionamento
CREATE TABLE IF NOT EXISTS horarios_funcionamento (
   id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_horarios_barbearia FOREIGN KEY (barbearia_id) 
        REFERENCES barbearias(id) ON DELETE CASCADE,
    
    -- Garante que não haja horários duplicados para o mesmo dia/horário
    CONSTRAINT uk_horarios_barbearia_dia_hora 
        UNIQUE (barbearia_id, dia_semana, hora_abertura)
);

-- Índices para melhorar performance nas consultas
CREATE INDEX IF NOT EXISTS idx_horarios_barbearia_id 
    ON horarios_funcionamento(barbearia_id);

CREATE INDEX IF NOT EXISTS idx_horarios_dia_semana 
    ON horarios_funcionamento(barbearia_id, dia_semana);

-- Comentários nas tabelas
COMMENT ON TABLE horarios_funcionamento IS 'Armazena os horários de funcionamento regulares das barbearias por dia da semana';
COMMENT ON COLUMN horarios_funcionamento.dia_semana IS 'Dia da semana (1=Segunda, 2=Terça, ..., 7=Domingo)';

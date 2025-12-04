-- ===========================================
-- Migration V7: Sistema de Bloqueios de Horários (Slots)
-- Sistema: Sua Barbearia
-- Adiciona controle granular de bloqueios de horários específicos
-- ===========================================

-- Tabela de Horários Bloqueados
CREATE TABLE IF NOT EXISTS horarios_bloqueados (
    id BIGSERIAL PRIMARY KEY,
    funcionario_id BIGINT NOT NULL,
    data DATE NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    motivo VARCHAR(255),
    criado_por VARCHAR(20) NOT NULL CHECK (criado_por IN ('BARBEARIA', 'PROFISSIONAL')),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key
    CONSTRAINT fk_horarios_bloqueados_funcionario 
        FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE,
    
    -- Evita bloqueios duplicados
    CONSTRAINT uk_horarios_bloqueados_funcionario_data_horario 
        UNIQUE (funcionario_id, data, horario_inicio, horario_fim)
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_bloqueios_funcionario_data 
    ON horarios_bloqueados(funcionario_id, data);

CREATE INDEX IF NOT EXISTS idx_bloqueios_funcionario_periodo 
    ON horarios_bloqueados(funcionario_id, data, horario_inicio, horario_fim);

-- Comentários de documentação
COMMENT ON TABLE horarios_bloqueados IS 'Armazena bloqueios específicos de slots de horários dos profissionais';
COMMENT ON COLUMN horarios_bloqueados.motivo IS 'Motivo do bloqueio (ex: Almoço, Reunião, Pausa)';
COMMENT ON COLUMN horarios_bloqueados.criado_por IS 'Quem criou o bloqueio: BARBEARIA ou PROFISSIONAL (via link)';

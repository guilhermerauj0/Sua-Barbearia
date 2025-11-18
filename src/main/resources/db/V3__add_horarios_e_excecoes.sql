-- ===========================================
-- Migration V3: Gestão de Horários de Funcionamento
-- Sistema: Sua Barbearia
-- Adiciona tabelas para horários e exceções (feriados/fechamentos)
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

-- Tabela de Exceções de Horário (Feriados, Fechamentos Especiais)
CREATE TABLE IF NOT EXISTS feriados_excecoes (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    data DATE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('FECHADO', 'HORARIO_ESPECIAL')),
    horario_abertura TIME NULL,
    horario_fechamento TIME NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_excecoes_barbearia FOREIGN KEY (barbearia_id) 
        REFERENCES barbearias(id) ON DELETE CASCADE,
    
    -- Garante que não haja exceções duplicadas para a mesma data
    CONSTRAINT uk_excecoes_barbearia_data 
        UNIQUE (barbearia_id, data)
);

-- Índices para melhorar performance nas consultas
CREATE INDEX IF NOT EXISTS idx_horarios_barbearia_id 
    ON horarios_funcionamento(barbearia_id);

CREATE INDEX IF NOT EXISTS idx_horarios_dia_semana 
    ON horarios_funcionamento(barbearia_id, dia_semana);

CREATE INDEX IF NOT EXISTS idx_excecoes_barbearia_id 
    ON feriados_excecoes(barbearia_id);

CREATE INDEX IF NOT EXISTS idx_excecoes_data 
    ON feriados_excecoes(barbearia_id, data);

CREATE INDEX IF NOT EXISTS idx_excecoes_periodo 
    ON feriados_excecoes(barbearia_id, data) WHERE ativo = TRUE;

-- Comentários nas tabelas
COMMENT ON TABLE horarios_funcionamento IS 'Armazena os horários de funcionamento regulares das barbearias por dia da semana';
COMMENT ON TABLE feriados_excecoes IS 'Armazena exceções aos horários normais (feriados, fechamentos especiais, horários diferenciados)';

COMMENT ON COLUMN horarios_funcionamento.dia_semana IS 'Dia da semana (1=Segunda, 2=Terça, ..., 7=Domingo)';
COMMENT ON COLUMN feriados_excecoes.tipo IS 'Tipo de exceção: FECHADO (estabelecimento fechado) ou HORARIO_ESPECIAL (horário diferenciado)';
COMMENT ON COLUMN feriados_excecoes.horario_abertura IS 'Hora de abertura para exceções do tipo HORARIO_ESPECIAL (NULL para FECHADO)';
COMMENT ON COLUMN feriados_excecoes.horario_fechamento IS 'Hora de fechamento para exceções do tipo HORARIO_ESPECIAL (NULL para FECHADO)';

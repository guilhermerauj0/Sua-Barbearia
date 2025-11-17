-- ===========================================
-- Script de inicialização do banco de dados
-- Sistema: Sua Barbearia
-- PostgreSQL
-- ===========================================

-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Barbearias
CREATE TABLE IF NOT EXISTS barbearias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    nome_fantasia VARCHAR(150) NOT NULL,
    tipo_documento VARCHAR(10) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'BARBEARIA',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Funcionários (Herança: JOINED)
CREATE TABLE IF NOT EXISTS funcionarios (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    profissao VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_funcionarios_barbearia FOREIGN KEY (barbearia_id) REFERENCES barbearias(id) ON DELETE CASCADE
);

-- Tabela de Serviços
CREATE TABLE IF NOT EXISTS servicos (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(10, 2) NOT NULL,
    duracao INTEGER NOT NULL DEFAULT 30,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_servicos_barbearia FOREIGN KEY (barbearia_id) REFERENCES barbearias(id) ON DELETE CASCADE
);

-- Tabela de Profissional_Servicos (many-to-many entre Funcionario e Servico)
CREATE TABLE IF NOT EXISTS profissional_servicos (
    id BIGSERIAL PRIMARY KEY,
    funcionario_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profissional_servicos_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_profissional_servicos_servico FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE CASCADE,
    CONSTRAINT uk_funcionario_servico UNIQUE (funcionario_id, servico_id)
);

-- Tabela de Agendamentos
CREATE TABLE IF NOT EXISTS agendamentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    barbearia_id BIGINT NOT NULL,
    barbeiro_id BIGINT,
    servico_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    observacoes VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agendamentos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_agendamentos_barbearia FOREIGN KEY (barbearia_id) REFERENCES barbearias(id) ON DELETE CASCADE,
    CONSTRAINT fk_agendamentos_barbeiro FOREIGN KEY (barbeiro_id) REFERENCES funcionarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_agendamentos_servico FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE RESTRICT
);

-- Tabela de Horários de Funcionamento
CREATE TABLE IF NOT EXISTS horarios_funcionamento (
    id BIGSERIAL PRIMARY KEY,
    barbearia_id BIGINT NOT NULL,
    dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_horarios_barbearia FOREIGN KEY (barbearia_id) REFERENCES barbearias(id) ON DELETE CASCADE,
    CONSTRAINT uk_barbearia_dia UNIQUE (barbearia_id, dia_semana)
);

-- Índices para melhor performance nas consultas
CREATE INDEX IF NOT EXISTS idx_clientes_email ON clientes(email);
CREATE INDEX IF NOT EXISTS idx_clientes_ativo ON clientes(ativo);

CREATE INDEX IF NOT EXISTS idx_barbearias_email ON barbearias(email);
CREATE INDEX IF NOT EXISTS idx_barbearias_ativo ON barbearias(ativo);

CREATE INDEX IF NOT EXISTS idx_funcionarios_barbearia ON funcionarios(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_funcionarios_email ON funcionarios(email);
CREATE INDEX IF NOT EXISTS idx_funcionarios_ativo ON funcionarios(ativo);

CREATE INDEX IF NOT EXISTS idx_servicos_barbearia ON servicos(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_servicos_ativo ON servicos(ativo);
CREATE INDEX IF NOT EXISTS idx_servicos_barbearia_ativo ON servicos(barbearia_id, ativo);

CREATE INDEX IF NOT EXISTS idx_profissional_servicos_funcionario ON profissional_servicos(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_profissional_servicos_servico ON profissional_servicos(servico_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_cliente ON agendamentos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_barbearia ON agendamentos(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_barbeiro ON agendamentos(barbeiro_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_servico ON agendamentos(servico_id);
CREATE INDEX IF NOT EXISTS idx_agendamentos_data_hora ON agendamentos(data_hora);
CREATE INDEX IF NOT EXISTS idx_agendamentos_status ON agendamentos(status);
CREATE INDEX IF NOT EXISTS idx_agendamentos_cliente_datahora ON agendamentos(cliente_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_agendamentos_barbearia_datahora ON agendamentos(barbearia_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_agendamentos_barbeiro_datahora ON agendamentos(barbeiro_id, data_hora);

CREATE INDEX IF NOT EXISTS idx_horarios_barbearia ON horarios_funcionamento(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_horarios_dia_semana ON horarios_funcionamento(dia_semana);
CREATE INDEX IF NOT EXISTS idx_horarios_ativo ON horarios_funcionamento(ativo);


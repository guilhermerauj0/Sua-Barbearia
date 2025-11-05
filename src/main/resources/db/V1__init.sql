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
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance nas consultas
CREATE INDEX IF NOT EXISTS idx_cliente_datahora ON agendamentos(cliente_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_barbearia_datahora ON agendamentos(barbearia_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_status ON agendamentos(status);
CREATE INDEX IF NOT EXISTS idx_servicos_barbearia ON servicos(barbearia_id);
CREATE INDEX IF NOT EXISTS idx_servicos_ativo ON servicos(ativo);
CREATE INDEX IF NOT EXISTS idx_servicos_barbearia_ativo ON servicos(barbearia_id, ativo);


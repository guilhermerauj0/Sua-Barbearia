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

-- ===========================================
-- Dados de teste
-- ===========================================

-- Inserir dados de teste apenas se não existirem
-- Senha: Senha@123 (BCrypt hash)

-- Clientes de teste
INSERT INTO clientes (nome, email, senha, telefone, role, ativo) 
SELECT 'João Silva Teste', 
       'joao.teste@email.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
       '11987654321',
       'CLIENTE',
       true
WHERE NOT EXISTS (
    SELECT 1 FROM clientes WHERE email = 'joao.teste@email.com'
);

-- Barbearia de teste
INSERT INTO barbearias (nome, email, senha, telefone, nome_fantasia, tipo_documento, documento, endereco, role, ativo)
SELECT 'Maria Silva',
       'maria.barbearia@email.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
       '11987654321',
       'Barbearia Teste',
       'CPF',
       '98765432100',
       'Rua Teste, 123 - São Paulo/SP',
       'BARBEARIA',
       true
WHERE NOT EXISTS (
    SELECT 1 FROM barbearias WHERE email = 'maria.barbearia@email.com'
);

-- Agendamentos de teste (histórico + futuros)
-- Inserir apenas se não existirem agendamentos para o cliente_id=1

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM agendamentos WHERE cliente_id = 1 LIMIT 1) THEN
        
        -- Agendamentos PASSADOS (histórico)
        INSERT INTO agendamentos (cliente_id, barbearia_id, barbeiro_id, servico_id, data_hora, status, observacoes, data_criacao, data_atualizacao)
        VALUES 
            (1, 1, NULL, 1, NOW() - INTERVAL '30 days', 'CONCLUIDO', 'Corte + barba - Primeiro atendimento', NOW() - INTERVAL '35 days', NOW() - INTERVAL '30 days'),
            (1, 1, NULL, 2, NOW() - INTERVAL '15 days', 'CONCLUIDO', 'Apenas corte de cabelo', NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days'),
            (1, 1, NULL, 1, NOW() - INTERVAL '7 days', 'CONCLUIDO', 'Corte + barba + sobrancelha', NOW() - INTERVAL '10 days', NOW() - INTERVAL '7 days'),
            (1, 1, NULL, 3, NOW() - INTERVAL '3 days', 'CANCELADO', 'Cliente cancelou - remarcou', NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days'),
            (1, 1, NULL, 2, NOW() - INTERVAL '1 day', 'CONCLUIDO', 'Corte social', NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'),
            
        -- Agendamentos FUTUROS (não devem aparecer no histórico)
            (1, 1, NULL, 1, NOW() + INTERVAL '2 days', 'CONFIRMADO', 'Agendamento confirmado para daqui 2 dias', NOW(), NOW()),
            (1, 1, NULL, 2, NOW() + INTERVAL '7 days', 'PENDENTE', 'Agendamento aguardando confirmação', NOW(), NOW());
            
    END IF;
END $$;

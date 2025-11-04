-- ===========================================
-- Script de dados de teste para Agendamentos
-- Sistema: Sua Barbearia
-- PostgreSQL
-- ===========================================

-- Inserir agendamentos de teste para o cliente ID 1
-- Misturando agendamentos passados e futuros para testar os filtros

-- Agendamentos PASSADOS (histórico)
INSERT INTO agendamentos (cliente_id, barbearia_id, barbeiro_id, servico_id, data_hora, status, observacoes, data_criacao, data_atualizacao)
VALUES 
    -- Agendamento concluído há 30 dias
    (1, 1, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '30 days', 'CONCLUIDO', 'Corte simples, cliente satisfeito', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '30 days'),
    
    -- Agendamento concluído há 15 dias
    (1, 1, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '15 days', 'CONCLUIDO', 'Corte + barba', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days'),
    
    -- Agendamento concluído há 7 dias
    (1, 2, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '7 days', 'CONCLUIDO', NULL, CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
    
    -- Agendamento cancelado há 20 dias
    (1, 1, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '20 days', 'CANCELADO', 'Cliente cancelou por imprevisto', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '20 days'),
    
    -- Agendamento concluído há 2 dias
    (1, 2, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', 'CONCLUIDO', 'Degradê + acabamento', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Agendamentos FUTUROS (não devem aparecer no histórico)
INSERT INTO agendamentos (cliente_id, barbearia_id, barbeiro_id, servico_id, data_hora, status, observacoes, data_criacao, data_atualizacao)
VALUES 
    -- Agendamento confirmado para daqui 3 dias
    (1, 1, NULL, 1, CURRENT_TIMESTAMP + INTERVAL '3 days', 'CONFIRMADO', 'Corte + barba agendado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Agendamento pendente para daqui 7 dias
    (1, 2, NULL, 1, CURRENT_TIMESTAMP + INTERVAL '7 days', 'PENDENTE', 'Aguardando confirmação', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Agendamentos de OUTROS CLIENTES (não devem aparecer para cliente ID 1)
INSERT INTO agendamentos (cliente_id, barbearia_id, barbeiro_id, servico_id, data_hora, status, observacoes, data_criacao, data_atualizacao)
VALUES 
    -- Cliente 2 - passado
    (2, 1, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '5 days', 'CONCLUIDO', 'Agendamento de outro cliente', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
    
    -- Cliente 3 - passado
    (3, 2, NULL, 1, CURRENT_TIMESTAMP - INTERVAL '10 days', 'CONCLUIDO', 'Mais um agendamento de outro cliente', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days');

-- Commit implícito (transação automática do Flyway)

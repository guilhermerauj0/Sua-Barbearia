-- Inserindo barbearia (senha: SenhaForte@123)
INSERT INTO barbearias (nome, email, senha, telefone, nome_fantasia, tipo_documento, documento, endereco, role, ativo, data_criacao, data_atualizacao)
VALUES 
('Maria Santos', 'maria.santos@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7ZhzJJDTqt5Ey2s2nqIxNjDDGTqZ.5G', '11987654321', 'Central Barber Shop', 'CNPJ', '12345678000199', 'Rua Central, 123', 'BARBEARIA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserindo cliente (senha: SenhaForte@123)
INSERT INTO clientes (nome, email, senha, telefone, role, ativo, data_criacao, data_atualizacao)
VALUES 
('João Silva', 'joao.silva@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7ZhzJJDTqt5Ey2s2nqIxNjDDGTqZ.5G', '11912345678', 'CLIENTE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserindo funcionários (diversos tipos)
INSERT INTO funcionarios (nome, email, telefone, barbearia_id, profissao, ativo, data_criacao, data_atualizacao)
VALUES 
('Carlos Barbeiro', 'carlos@barbearia.com', '11998765432', 1, 'BARBEIRO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ana Manicure', 'ana@barbearia.com', '11987654321', 1, 'MANICURE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Paula Esteticista', 'paula@barbearia.com', '11976543210', 1, 'ESTETICISTA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Roberto Colorista', 'roberto@barbearia.com', '11965432109', 1, 'COLORISTA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserindo nas tabelas de subclasses de funcionário
INSERT INTO funcionarios_barbeiro (id) VALUES (1);
INSERT INTO funcionarios_manicure (id) VALUES (2);
INSERT INTO funcionarios_esteticista (id) VALUES (3);
INSERT INTO funcionarios_colorista (id) VALUES (4);

-- Inserindo serviços (todos os tipos disponíveis)
INSERT INTO servicos (barbearia_id, nome, descricao, preco, duracao, tipo_servico, ativo, data_criacao, data_atualizacao)
VALUES 
(1, 'Corte de Cabelo', 'Corte masculino tradicional', 50.00, 30, 'CORTE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Barba', 'Barba completa com toalha quente', 40.00, 20, 'BARBA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Corte + Barba', 'Pacote completo corte e barba', 80.00, 45, 'CORTE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Sobrancelha', 'Designer de sobrancelha', 25.00, 15, 'SOBRANCELHA', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Coloração', 'Coloração completa de cabelo', 120.00, 60, 'COLORACAO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Manicure', 'Manicure completa com esmaltação', 35.00, 30, 'MANICURE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Tratamento Capilar', 'Hidratação e tratamento para cabelos', 90.00, 45, 'TRATAMENTO_CAPILAR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserindo nas tabelas de subclasses de serviço
INSERT INTO servicos_corte (id) VALUES (1);
INSERT INTO servicos_barba (id) VALUES (2);
INSERT INTO servicos_corte (id) VALUES (3);
INSERT INTO servicos_sobrancelha (id) VALUES (4);
INSERT INTO servicos_coloracao (id) VALUES (5);
INSERT INTO servicos_manicure (id) VALUES (6);
INSERT INTO servicos_tratamento_capilar (id) VALUES (7);

-- Vinculando profissional aos serviços
-- Carlos Barbeiro: Corte, Barba, Corte+Barba
INSERT INTO profissional_servicos (funcionario_id, servico_id)
VALUES 
(1, 1),
(1, 2),
(1, 3);

-- Ana Manicure: Manicure
INSERT INTO profissional_servicos (funcionario_id, servico_id)
VALUES 
(2, 6);

-- Paula Esteticista: Sobrancelha
INSERT INTO profissional_servicos (funcionario_id, servico_id)
VALUES 
(3, 4);

-- Roberto Colorista: Coloração, Tratamento Capilar
INSERT INTO profissional_servicos (funcionario_id, servico_id)
VALUES 
(4, 5),
(4, 7);

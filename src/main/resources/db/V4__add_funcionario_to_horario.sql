-- Adiciona coluna funcionario_id na tabela horarios_funcionamento
ALTER TABLE horarios_funcionamento ADD COLUMN funcionario_id BIGINT;

-- Adiciona constraint de chave estrangeira
ALTER TABLE horarios_funcionamento ADD CONSTRAINT fk_horarios_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE;

-- Remove a constraint unique antiga
ALTER TABLE horarios_funcionamento DROP CONSTRAINT uk_barbearia_dia;

-- Adiciona nova constraint unique considerando funcionario_id
-- Se funcionario_id for NULL, é o horário da barbearia (default)
-- Se funcionario_id for preenchido, é o horário específico do funcionário
CREATE UNIQUE INDEX uk_horario_barbearia_funcionario_dia ON horarios_funcionamento (barbearia_id, dia_semana, COALESCE(funcionario_id, -1));

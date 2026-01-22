-- Script SQL para popular o banco de dados com dados de exemplo
-- Execute após a aplicação criar as tabelas automaticamente

USE controle_gastos;

-- Inserir categorias padrão (senha: admin123)
INSERT INTO usuarios (nome, email, senha, role, ativo, created_at, updated_at) VALUES
('Administrador', 'admin@controlegastos.com', '$2a$10$X1234567890abcdefghijklmnopqrstuvwxyzABCDEFGH', 'ADMIN', true, NOW(), NOW());

-- Inserir usuário teste (senha: senha123)
INSERT INTO usuarios (nome, email, senha, role, ativo, created_at, updated_at) VALUES
('João Silva', 'joao@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', true, NOW(), NOW());

-- Inserir categorias
INSERT INTO categorias (nome, descricao, cor, icone, created_at, updated_at) VALUES
('Alimentação', 'Gastos com alimentação em geral', '#E74C3C', 'restaurant', NOW(), NOW()),
('Transporte', 'Combustível, táxi, ônibus, metrô', '#3498DB', 'directions_car', NOW(), NOW()),
('Moradia', 'Aluguel, condomínio, contas da casa', '#9B59B6', 'home', NOW(), NOW()),
('Saúde', 'Médicos, medicamentos, plano de saúde', '#1ABC9C', 'local_hospital', NOW(), NOW()),
('Lazer', 'Cinema, restaurantes, viagens', '#F39C12', 'beach_access', NOW(), NOW()),
('Educação', 'Cursos, livros, material escolar', '#2ECC71', 'school', NOW(), NOW()),
('Vestuário', 'Roupas e calçados', '#E67E22', 'shopping_bag', NOW(), NOW()),
('Outros', 'Gastos diversos', '#95A5A6', 'more_horiz', NOW(), NOW());

-- Inserir gastos de exemplo para o usuário João (ID 2)
INSERT INTO gastos (descricao, valor, categoria_id, usuario_id, data_gasto, observacao, created_at, updated_at) VALUES
-- Janeiro 2024
('Almoço no restaurante', 85.50, 1, 2, '2024-01-05', 'Restaurante japonês', NOW(), NOW()),
('Supermercado', 450.00, 1, 2, '2024-01-08', 'Compras do mês', NOW(), NOW()),
('Uber para o trabalho', 35.00, 2, 2, '2024-01-10', NULL, NOW(), NOW()),
('Combustível', 300.00, 2, 2, '2024-01-12', 'Tanque cheio', NOW(), NOW()),
('Aluguel', 2500.00, 3, 2, '2024-01-15', 'Janeiro/2024', NOW(), NOW()),
('Conta de luz', 280.00, 3, 2, '2024-01-18', NULL, NOW(), NOW()),
('Consulta médica', 250.00, 4, 2, '2024-01-20', 'Clínico geral', NOW(), NOW()),
('Cinema', 80.00, 5, 2, '2024-01-22', 'Filme com a família', NOW(), NOW()),
('Livro técnico', 120.00, 6, 2, '2024-01-25', 'Java Avançado', NOW(), NOW()),
('Tênis esportivo', 350.00, 7, 2, '2024-01-28', 'Nike', NOW(), NOW());

-- Consultas úteis para validação

-- Total de gastos por categoria
SELECT 
    c.nome as categoria,
    COUNT(g.id) as quantidade,
    SUM(g.valor) as total
FROM categorias c
LEFT JOIN gastos g ON c.id = g.categoria_id
WHERE g.usuario_id = 2
GROUP BY c.id, c.nome
ORDER BY total DESC;

-- Gastos do mês atual
SELECT 
    g.data_gasto,
    g.descricao,
    g.valor,
    c.nome as categoria
FROM gastos g
INNER JOIN categorias c ON g.categoria_id = c.id
WHERE g.usuario_id = 2
  AND MONTH(g.data_gasto) = MONTH(CURRENT_DATE())
  AND YEAR(g.data_gasto) = YEAR(CURRENT_DATE())
ORDER BY g.data_gasto DESC;

-- Total geral de gastos do usuário
SELECT 
    u.nome as usuario,
    COUNT(g.id) as total_gastos,
    SUM(g.valor) as valor_total
FROM usuarios u
LEFT JOIN gastos g ON u.id = g.usuario_id
WHERE u.id = 2
GROUP BY u.id, u.nome;

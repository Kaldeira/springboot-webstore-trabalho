USE webstore;

INSERT INTO categorias (nome, descricao) VALUES
  ('Camisetas',  'Camisetas masculinas e femininas'),
  ('Calças',     'Calças jeans, jogger e legging'),
  ('Tênis',      'Tênis casuais e esportivos'),
  ('Moletons',   'Moletons e hoodies');

INSERT INTO produtos (categoria_id, nome, descricao, preco) VALUES
  (1, 'Camiseta Básica Branca',   'Algodão 100%, corte regular.',          49.90),
  (1, 'Camiseta Oversized Preta', 'Fit oversized, algodão premium.',        79.90),
  (2, 'Calça Jeans Slim',         'Jeans stretch, corte slim.',            159.90),
  (2, 'Calça Jogger Cinza',       'Moletom leve, elástico na cintura.',    119.90),
  (3, 'Tênis Air Casual Branco',  'Solado EVA, cabedal em mesh.',          299.90),
  (3, 'Tênis Running Pro',        'Amortecimento reativo, corrida.',       449.90),
  (4, 'Moletom Canguru Azul',     'Fleece interno macio, capuz duplo.',   189.90);

INSERT INTO variantes (produto_id, tamanho, cor, estoque) VALUES
  -- Camiseta Básica Branca
  (1, 'P',  'Branco', 30), (1, 'M',  'Branco', 50), (1, 'G',  'Branco', 40), (1, 'GG', 'Branco', 20),
  -- Camiseta Oversized Preta
  (2, 'P',  'Preto',  15), (2, 'M',  'Preto',  30), (2, 'G',  'Preto',  25),
  -- Calça Jeans Slim
  (3, '38', 'Azul',   10), (3, '40', 'Azul',   15), (3, '42', 'Azul',   12), (3, '44', 'Azul',   8),
  -- Calça Jogger Cinza
  (4, 'P',  'Cinza',  20), (4, 'M',  'Cinza',  30), (4, 'G',  'Cinza',  20),
  -- Tênis Air Casual Branco
  (5, '38', 'Branco', 8), (5, '39', 'Branco', 10), (5, '40', 'Branco', 12),
  (5, '41', 'Branco', 10), (5, '42', 'Branco', 7),
  -- Tênis Running Pro
  (6, '39', 'Preto',  5), (6, '40', 'Preto',  8), (6, '41', 'Preto', 10), (6, '42', 'Preto', 6),
  -- Moletom Canguru Azul
  (7, 'P',  'Azul',  10), (7, 'M',  'Azul',  18), (7, 'G',  'Azul',  14), (7, 'GG', 'Azul',  8);

INSERT INTO imagens_produto (produto_id, url, principal) VALUES
  (1, '/images/camiseta-basica-branca-1.jpg', TRUE),
  (2, '/images/camiseta-oversized-preta-1.jpg', TRUE),
  (3, '/images/calca-jeans-slim-1.jpg', TRUE),
  (4, '/images/calca-jogger-cinza-1.jpg', TRUE),
  (5, '/images/tenis-air-casual-1.jpg', TRUE),
  (6, '/images/tenis-running-pro-1.jpg', TRUE),
  (7, '/images/moletom-canguru-azul-1.jpg', TRUE);

INSERT INTO usuarios (nome, email, senha_hash, cpf, telefone, tipo) VALUES
  ('Ana Lima',    'ana@email.com',    '1234', '111.111.111-11', '(11) 91111-1111', 1),
  ('Carlos Souza','carlos@email.com', '1234', '222.222.222-22', '(21) 92222-2222', 1),
  ('Admin','admin@email.com', 'admin', '233.222.223-22', '(21) 92322-2322', 0);

INSERT INTO enderecos (usuario_id, apelido, logradouro, numero, bairro, cidade, estado, cep, principal) VALUES
  (1, 'Casa',     'Rua das Flores',   '123', 'Centro',       'São Paulo',   'SP', '01310-100', TRUE),
  (2, 'Casa',     'Av. Atlântica',    '456', 'Copacabana',   'Rio de Janeiro', 'RJ', '22021-001', TRUE);

INSERT INTO pedidos (usuario_id, endereco_id, status, total, frete) VALUES
  (1, 1, 'PAGO', 399.70, 19.90);

INSERT INTO itens_pedido (pedido_id, variante_id, quantidade, preco_unit) VALUES
  (1, 2,  1, 49.90),   -- Camiseta Básica M
  (1, 15, 1, 299.90),  -- Tênis Air Casual 40
  (1, 13, 1, 49.90);   -- Camiseta Básica G (ex.)

INSERT INTO pagamentos (pedido_id, metodo, status, valor, id_transacao, pago_em) VALUES
  (1, 'PIX', 'APROVADO', 399.70, 'TXN-ABC123', NOW());

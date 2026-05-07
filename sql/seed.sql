USE webstore;

INSERT INTO categorias (nome, descricao, colecao) VALUES
  ('Camisetas',  'Camisetas masculinas e femininas da coleção Divino', 'Divino'),
  ('Camisetas',  'Camisetas masculinas e femininas da coleção Alucard', 'Alucard'),
  ('Camisetas',  'Camisetas masculinas e femininas da coleção Morte', 'Morte'),
  ('Calças',     'Calças jeans, jogger e legging da coleção Divino', 'Divino'),
  ('Calças',     'Calças jeans, jogger e legging da coleção Alucard', 'Alucard'),
  ('Calças',     'Calças jeans, jogger e legging da coleção Morte', 'Morte'),
  ('Shorts',     'Shorts da coleção Alucard', 'Alucard'),
  ('Shorts',     'Shorts da coleção Morte', 'Morte'),
  ('Shorts',     'Shorts da coleção Divino', 'Divino'),
  ('Moletons',   'Moletons e hoodies da coleção Alucard', 'Alucard'),
  ('Moletons',   'Moletons e hoodies da coleção Divino', 'Divino'),
  ('Moletons',   'Moletons e hoodies da coleção Morte', 'Morte');


INSERT INTO produtos (categoria_id, nome, descricao, preco) VALUES
  -- CAMISETAS
  (1, 'Camiseta Divino Gold',        'Estampa premium dourada, algodão fio 30.',         89.90),
  (2, 'Camiseta Alucard Blood',      'Modelagem streetwear com estampa dark.',            99.90),
  (3, 'Camiseta Morte Skull',        'Camiseta preta com arte caveira exclusiva.',        94.90),

  -- CALÇAS
  (4, 'Calça Divino Jogger White',   'Jogger confortável com ajuste slim.',              149.90),
  (5, 'Calça Alucard Cargo Black',   'Calça cargo oversized estilo urbano.',             189.90),
  (6, 'Calça Morte Destroyed',       'Jeans destroyed com lavagem escura.',              179.90),

  -- SHORTS
  (7, 'Shorts Alucard Shadow',       'Shorts esportivo leve com bolso lateral.',          89.90),
  (8, 'Shorts Morte Dark Flame',     'Shorts preto estampado coleção Morte.',             94.90),
  (9, 'Shorts Divino Heaven',        'Shorts casual azul claro premium.',                 84.90),

  -- MOLETONS
  (10, 'Moletom Alucard Red Moon',   'Moletom fechado com capuz e fleece interno.',      229.90),
  (11, 'Moletom Divino Angel',       'Hoodie branco coleção Divino.',                     239.90),
  (12, 'Moletom Morte Raven',        'Moletom oversized preto coleção Morte.',           249.90);


INSERT INTO variantes (produto_id, tamanho, cor, estoque) VALUES

  -- Camiseta Divino Gold
  (1,  'P',  'Branco', 20),
  (1,  'M',  'Branco', 35),
  (1,  'G',  'Branco', 30),
  (1,  'GG', 'Branco', 15),

  -- Camiseta Alucard Blood
  (2,  'P',  'Preto', 18),
  (2,  'M',  'Preto', 25),
  (2,  'G',  'Preto', 22),
  (2,  'GG', 'Preto', 10),

  -- Camiseta Morte Skull
  (3, 'P',  'Preto', 12),
  (3, 'M',  'Preto', 20),
  (3, 'G',  'Preto', 18),
  (3, 'GG', 'Preto', 8),

  -- Calça Divino Jogger White
  (4, '38', 'Branco', 10),
  (4, '40', 'Branco', 15),
  (4, '42', 'Branco', 12),
  (4, '44', 'Branco', 8),

  -- Calça Alucard Cargo Black
  (5, '38', 'Preto', 8),
  (5, '40', 'Preto', 12),
  (5, '42', 'Preto', 10),
  (5, '44', 'Preto', 6),

  -- Calça Morte Destroyed
  (6, '38', 'Grafite', 7),
  (6, '40', 'Grafite', 11),
  (6, '42', 'Grafite', 9),
  (6, '44', 'Grafite', 5),

  -- Shorts Alucard Shadow
  (7, 'P',  'Preto', 14),
  (7, 'M',  'Preto', 20),
  (7, 'G',  'Preto', 18),

  -- Shorts Morte Dark Flame
  (8, 'P',  'Preto', 12),
  (8, 'M',  'Preto', 18),
  (8, 'G',  'Preto', 15),

  -- Shorts Divino Heaven
  (9, 'P',  'Azul', 16),
  (9, 'M',  'Azul', 22),
  (9, 'G',  'Azul', 18),

  -- Moletom Alucard Red Moon
  (10, 'P',  'Vermelho', 8),
  (10, 'M',  'Vermelho', 14),
  (10, 'G',  'Vermelho', 12),
  (10, 'GG', 'Vermelho', 6),

  -- Moletom Divino Angel
  (11, 'P',  'Branco', 10),
  (11, 'M',  'Branco', 16),
  (11, 'G',  'Branco', 14),
  (11, 'GG', 'Branco', 7),

  -- Moletom Morte Raven
  (12, 'P',  'Preto', 9),
  (12, 'M',  'Preto', 15),
  (12, 'G',  'Preto', 13),
  (12, 'GG', 'Preto', 6);


INSERT INTO imagens_produto (produto_id, url, principal) VALUES
  (1,  '/uploads/camiseta-divino-gold.png', TRUE),
  (2,  '/uploads/camiseta-alucard-blood.png', TRUE),
  (3, '/uploads/camiseta-morte-skull.png', TRUE),

  (4, '/uploads/calca-divino-jogger-white.png', TRUE),
  (5, '/uploads/calca-alucard-cargo-black.png', TRUE),
  (6, '/uploads/calca-morte-destroyed.png', TRUE),

  (7, '/uploads/shorts-alucard-shadow.png', TRUE),
  (8, '/uploads/shorts-morte-dark-flame.png', TRUE),
  (9, '/uploads/shorts-divino-heaven.png', TRUE),

  (10, '/uploads/moletom-alucard-red-moon.png', TRUE),
  (11, '/uploads/moletom-divino-angel.png', TRUE),
  (12, '/uploads/moletom-morte-raven.png', TRUE);

INSERT INTO usuarios (nome, email, senha_hash, cpf, telefone, tipo) VALUES
  ('Ana Lima',    'ana@email.com',    '1234', '111.111.111-11', '(11) 91111-1111', 1),
  ('Carlos Souza','funcionario', '123', '222.222.222-22', '(21) 92222-2222', 2),
  ('Admin','admin', 'admin', '233.222.223-22', '(21) 92322-2322', 0);

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

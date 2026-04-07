DROP DATABASE IF EXISTS webstore;
CREATE DATABASE IF NOT EXISTS webstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webstore;

CREATE TABLE categorias ( -- categorias (roupas, tênis, acessórios, etc.)
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    descricao  TEXT,
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE produtos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT NOT NULL,
    nome         VARCHAR(200) NOT NULL,
    descricao    TEXT,
    preco        DECIMAL(10, 2) NOT NULL,
    ativo        BOOLEAN NOT NULL DEFAULT TRUE, -- se disponivel
    criado_em    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE variantes ( -- variantes do produto (tamanho + cor)
    id         INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    tamanho    VARCHAR(20) NOT NULL,   -- ex: P, M, G, 38, 40
    cor        VARCHAR(50) NOT NULL,
    estoque    INT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT fk_variante_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE imagens_produto (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    url        VARCHAR(500) NOT NULL, -- url pra foto no servidor
    principal  BOOLEAN NOT NULL DEFAULT FALSE, -- primeira foto
    CONSTRAINT fk_imagem_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE usuarios (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    senha_hash  VARCHAR(255) NOT NULL, -- usar md5 ou sha256
    cpf         VARCHAR(14) UNIQUE,
    telefone    VARCHAR(20),
    tipo        TINYINT NOT NULL DEFAULT 1 CHECK (tipo IN (0, 1, 2)), -- COMMENT '0=admin,1=comum,2=funcionario'
    criado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enderecos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT NOT NULL, -- id do usuario
    apelido     VARCHAR(50),           -- ex: "Casa", "Trabalho"
    logradouro  VARCHAR(200) NOT NULL,
    numero      VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    bairro      VARCHAR(100) NOT NULL,
    cidade      VARCHAR(100) NOT NULL,
    estado      CHAR(2) NOT NULL,
    cep         VARCHAR(10) NOT NULL,
    principal   BOOLEAN NOT NULL DEFAULT FALSE, 
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE pedidos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      INT NOT NULL, -- usuario_id
    endereco_id     INT NOT NULL,
    status          ENUM('AGUARDANDO_PAGAMENTO','PAGO','EM_SEPARACAO',
                         'ENVIADO','ENTREGUE','CANCELADO') NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    total           DECIMAL(10, 2) NOT NULL,
    frete           DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    codigo_rastreio VARCHAR(50),
    criado_em       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_cliente   FOREIGN KEY (usuario_id)  REFERENCES usuarios(id),
    CONSTRAINT fk_pedido_endereco  FOREIGN KEY (endereco_id) REFERENCES enderecos(id)
);

CREATE TABLE itens_pedido (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id   INT NOT NULL,
    variante_id INT NOT NULL,
    quantidade  INT NOT NULL,
    preco_unit  DECIMAL(10, 2) NOT NULL,  -- preço no momento da compra
    CONSTRAINT fk_item_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedidos(id),
    CONSTRAINT fk_item_variante FOREIGN KEY (variante_id) REFERENCES variantes(id)
);

CREATE TABLE pagamentos ( -- n sei se vou chegar a usar isso aqui
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT NOT NULL UNIQUE,
    metodo          ENUM('CARTAO_CREDITO','PIX','BOLETO') NOT NULL,
    status          ENUM('PENDENTE','APROVADO','RECUSADO','ESTORNADO') NOT NULL DEFAULT 'PENDENTE',
    valor           DECIMAL(10, 2) NOT NULL,
    id_transacao    VARCHAR(100),           -- ID retornado pelo gateway
    pago_em         TIMESTAMP,
    CONSTRAINT fk_pagamento_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

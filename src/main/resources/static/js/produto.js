var _galIdx = 0;
var _varianteAtual = null;
var _qtdProd = 1;

function _getVariantes() {
    return VARIANTES_DATA[PRODUTO_ID] || [];
}

function _getEstoqueVariante(variante) {
    if (!variante) return 0;
    return Number(variante.estoque || 0);
}

function _getCartQtdVariante(variante) {
    if (!variante) return 0;

    var cart = getCart();
    var key = PRODUTO_ID + '-' + variante.id;

    var item = cart.find(function(i) {
        return i.key === key;
    });

    return item ? Number(item.quantidade || 0) : 0;
}

function _getDisponivelVariante(variante) {
    var estoque = _getEstoqueVariante(variante);
    var jaNoCarrinho = _getCartQtdVariante(variante);

    return Math.max(0, estoque - jaNoCarrinho);
}

function _setQtdProd(qtd) {
    _qtdProd = Math.max(1, Number(qtd || 1));

    var el = document.getElementById('prod-qty');
    if (el) el.textContent = _qtdProd;
}

function _mostrarErroProduto(msg) {
    var err = document.getElementById('prod-err');
    if (!err) return;

    err.textContent = msg;
    err.style.display = 'block';
}

function _esconderErroProduto() {
    var err = document.getElementById('prod-err');
    if (!err) return;

    err.style.display = 'none';
}

function _galeriaSync(idx) {
    if (!IMAGENS_URLS.length) return;

    _galIdx = (idx + IMAGENS_URLS.length) % IMAGENS_URLS.length;

    var img = document.getElementById('galeria-img-principal');
    if (img) img.src = IMAGENS_URLS[_galIdx];

    document.querySelectorAll('.galeria-dot').forEach(function(el, i) {
        el.classList.toggle('active', i === _galIdx);
    });

    document.querySelectorAll('.galeria-thumb').forEach(function(el, i) {
        el.classList.toggle('active', i === _galIdx);
    });
}

function galeriaNav(dir) {
    _galeriaSync(_galIdx + dir);
}

function galeriaIr(idx) {
    _galeriaSync(idx);
}

function toggleTabela(btn) {
    var tabela = document.getElementById('tabela-tamanhos');
    var open = tabela.classList.toggle('open');

    btn.classList.toggle('open', open);
}

function _buildTamanhoOpts() {
    var container = document.getElementById('tamanho-opts');
    if (!container) return;

    container.innerHTML = '';

    _getVariantes().forEach(function(v) {

        // evita repetir tamanho
        if (container.querySelector('[data-tamanho="' + v.tamanho + '"]')) return;

        var estoque = _getEstoqueVariante(v);

        var btn = document.createElement('button');

        btn.type = 'button';
        btn.className = 'var-btn' + (estoque <= 0 ? ' esgotado' : '');
        btn.dataset.tamanho = v.tamanho;
        btn.dataset.estoque = estoque;

        if (estoque <= 0) {
            btn.disabled = true;
            btn.textContent = v.tamanho + ' - Esgotado';
        } else {
            btn.textContent = v.tamanho;
        }

        btn.onclick = function() {
            selecionarTamanho(btn, v);
        };

        container.appendChild(btn);
    });
}

function selecionarTamanho(btn, variante) {
    document.querySelectorAll('.var-btn').forEach(function(b) {
        b.classList.remove('selected');
    });

    btn.classList.add('selected');

    _varianteAtual = variante;

    var txt = document.getElementById('tam-selected');
    if (txt) {
        txt.textContent = variante.tamanho;
    }

    var disponivel = _getDisponivelVariante(variante);

    var estoqueInfo = document.getElementById('produto-estoque-info');
    if (estoqueInfo) {
        estoqueInfo.textContent = 'Disponível: ' + disponivel + ' unidade(s)';
    }

    if (disponivel <= 0) {
        _setQtdProd(1);
        _mostrarErroProduto('Você já adicionou todo o estoque desse tamanho no carrinho');
        return;
    }

    _setQtdProd(1);
    _esconderErroProduto();
}

function alterarQtyProd(delta) {
    if (!_varianteAtual) {
        _mostrarErroProduto('Selecione um tamanho para continuar');
        return;
    }

    var disponivel = _getDisponivelVariante(_varianteAtual);

    if (disponivel <= 0) {
        _setQtdProd(1);
        _mostrarErroProduto('Não há mais estoque disponível para esse tamanho');
        return;
    }

    var novaQtd = _qtdProd + delta;

    if (novaQtd < 1) {
        novaQtd = 1;
    }

    if (novaQtd > disponivel) {
        novaQtd = disponivel;
        _mostrarErroProduto('Quantidade máxima disponível: ' + disponivel);
    } else {
        _esconderErroProduto();
    }

    _setQtdProd(novaQtd);
}

function addProdutoCarrinho() {

    if (!_varianteAtual) {
        _mostrarErroProduto('Selecione um tamanho para continuar');
        return;
    }

    var estoque = _getEstoqueVariante(_varianteAtual);
    var jaNoCarrinho = _getCartQtdVariante(_varianteAtual);
    var disponivel = Math.max(0, estoque - jaNoCarrinho);

    if (estoque <= 0) {
        _mostrarErroProduto('Esse tamanho está esgotado');
        return;
    }

    if (disponivel <= 0) {
        _mostrarErroProduto('Você já adicionou todo o estoque desse tamanho no carrinho');
        return;
    }

    if (_qtdProd > disponivel) {
        _setQtdProd(disponivel);
        _mostrarErroProduto('Quantidade ajustada para o máximo disponível: ' + disponivel);
        return;
    }

    var cart = getCart();

    var key = PRODUTO_ID + '-' + _varianteAtual.id;

    var item = cart.find(function(i) {
        return i.key === key;
    });

    var img = document.getElementById('galeria-img-principal');

    if (item) {

        item.quantidade += _qtdProd;

        if (item.quantidade > estoque) {
            item.quantidade = estoque;
        }

    } else {

        cart.push({
            key: key,
            produtoId: PRODUTO_ID,
            nome: document.querySelector('.produto-nome').textContent.trim(),
            preco: Number(PRODUTO_PRECO),
            img: img ? img.src : '',
            variante: _varianteAtual.tamanho,
            varianteId: _varianteAtual.id,
            estoque: estoque,
            quantidade: _qtdProd
        });

    }

    saveCart(cart);

    atualizarContador();

    _esconderErroProduto();

    abrirCarrinho();
}
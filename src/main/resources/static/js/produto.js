var _galIdx = 0;
var _varianteAtual = null;
var _qtdProd = 1;

function _getVariantes() {
    return VARIANTES_DATA[PRODUTO_ID] || [];
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

        var btn = document.createElement('button');

        btn.type = 'button';
        btn.className = 'var-btn' + (v.estoque <= 0 ? ' esgotado' : '');
        btn.textContent = v.tamanho;
        btn.dataset.tamanho = v.tamanho;

        if (v.estoque <= 0) btn.disabled = true;

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
    if (txt) txt.textContent = variante.tamanho;

    var err = document.getElementById('prod-err');
    if (err) err.style.display = 'none';
}

function alterarQtyProd(delta) {
    _qtdProd = Math.max(1, _qtdProd + delta);

    var el = document.getElementById('prod-qty');
    if (el) el.textContent = _qtdProd;
}

function addProdutoCarrinho() {

    if (!_varianteAtual) {
        var err = document.getElementById('prod-err');
        if (err) err.style.display = 'block';
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

    } else {

        cart.push({
            key: key,
            produtoId: PRODUTO_ID,
            nome: document.querySelector('.produto-nome').textContent.trim(),
            preco: Number(PRODUTO_PRECO),
            img: img ? img.src : '',
            variante: _varianteAtual.tamanho,
            varianteId: _varianteAtual.id,
            quantidade: _qtdProd
        });

    }

    saveCart(cart);

    atualizarContador();

    abrirCarrinho();
}
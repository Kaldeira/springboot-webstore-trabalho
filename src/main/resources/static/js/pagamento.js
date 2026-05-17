var metodoPagamento = 'PIX';

function selecionarMetodo(metodo, el) {
    metodoPagamento = metodo;
    document.querySelectorAll('.metodo-opcao').forEach(function(m) { m.classList.remove('selected'); });
    el.classList.add('selected');
    document.getElementById('pix-info').style.display = metodo === 'PIX' ? '' : 'none';
    document.getElementById('cartao-info').style.display = metodo === 'CARTAO' ? '' : 'none';
    atualizarTotalFinal();
}

function fmt(val) { return 'R$ ' + val.toFixed(2).replace('.', ','); }

function atualizarTotalFinal() {
    var cart = getCart();
    var subtotal = cart.reduce(function(s, i) { return s + i.preco * i.quantidade; }, 0);
    var desconto = metodoPagamento === 'PIX' ? subtotal * 0.05 : 0;
    var frete = subtotal >= 499 ? 0 : 29.90;
    var total = subtotal - desconto + frete;
    var tv = document.getElementById('total-final-val');
    if (tv) tv.textContent = fmt(total);
    return { subtotal: subtotal, desconto: desconto, frete: frete, total: total };
}

function renderizarItens() {
    var cart = getCart();
    var list = document.getElementById('checkout-items-list');
    var totals = document.getElementById('checkout-totals');
    if (!list) return;

    if (!cart.length) {
        list.innerHTML = '<div class="checkout-empty">Carrinho vazio. <a href="/" style="color:var(--accent)">Voltar à loja</a></div>';
        return;
    }

    var html = '';
    var subtotal = 0;
    cart.forEach(function(item) {
        subtotal += item.preco * item.quantidade;
        var imgHtml = item.img
            ? '<img src="' + item.img + '" alt="' + item.nome + '"/>'
            : '<div class="co-no-img">' + item.nome.substring(0,3).toUpperCase() + '</div>';
        html += '<div class="co-item">'
            + '<div class="co-item-img">' + imgHtml + '</div>'
            + '<div class="co-item-info">'
            + '<div class="co-item-nome">' + item.nome + '</div>'
            + '<div class="co-item-variante">' + item.variante + '</div>'
            + '<div class="co-item-qty">Qtd: ' + item.quantidade + '</div>'
            + '</div>'
            + '<div class="co-item-preco">' + fmt(item.preco * item.quantidade) + '</div>'
            + '</div>';
    });
    list.innerHTML = html;

    if (totals) totals.style.display = '';
    var frete = subtotal >= 499 ? 0 : 29.90;
    var sub = document.getElementById('co-subtotal');
    var fr = document.getElementById('co-frete');
    var total = document.getElementById('co-total');
    if (sub) sub.textContent = fmt(subtotal);
    if (fr) fr.textContent = frete === 0 ? 'Grátis' : fmt(frete);
    if (frete) total.textContent = fmt((subtotal + frete));
    atualizarTotalFinal();
}

function selecionarEndereco(el) {
    document.querySelectorAll('.endereco-opcao').forEach(function(e) { e.classList.remove('selected'); });
    el.classList.add('selected');
    var endId = document.getElementById('endereco-id');
    if (endId) endId.value = el.dataset.id;
}

function toggleEndereco(btn) {
    var content = btn.nextElementSibling;
    content.classList.toggle('open');
    btn.querySelector('i').className = content.classList.contains('open')
        ? 'fa-solid fa-minus' : 'fa-solid fa-plus';
}

function getEnderecoId() {
    var el = document.getElementById('endereco-id');
    return el ? el.value : null;
}

function iniciarPagamento() {
    var cart = getCart();
    if (!cart.length) {
        alert('Seu carrinho está vazio.');
        return;
    }

    var enderecoId = getEnderecoId();
    var novoEndereco = null;

    if (!enderecoId) {
        var log = document.getElementById('co-logradouro');
        var num = document.getElementById('co-numero');
        var bai = document.getElementById('co-bairro');
        var cid = document.getElementById('co-cidade');
        var est = document.getElementById('co-estado');
        var cep = document.getElementById('co-cep');

        if (!log || !log.value || !num || !num.value || !bai || !bai.value
            || !cid || !cid.value || !est || !est.value || !cep || !cep.value) {
            alert('Preencha o endereço de entrega.');
            return;
        }

        novoEndereco = {
            logradouro: log.value,
            numero: num.value,
            complemento: document.getElementById('co-complemento') ? document.getElementById('co-complemento').value : '',
            bairro: bai.value,
            cidade: cid.value,
            estado: est.value,
            cep: cep.value,
            apelido: document.getElementById('co-apelido') ? document.getElementById('co-apelido').value : ''
        };
    }

    var valores = atualizarTotalFinal();
    var payload = {
        itens: cart,
        metodo: metodoPagamento,
        enderecoId: enderecoId,
        novoEndereco: novoEndereco,
        subtotal: valores.subtotal,
        desconto: valores.desconto,
        frete: valores.frete,
        total: valores.total
    };

    var btn = document.getElementById('btn-pagar');
    var btnText = document.getElementById('btn-pagar-text');
    if (btn) btn.disabled = true;
    if (btnText) btnText.textContent = 'Processando…';

    fetch('/checkout/iniciar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(function(r) { return r.json(); })
        .then(function(data) {

            // PIX aprovado automaticamente
            if (data.pixAprovado) {

                localStorage.removeItem('nemesis_cart_v1');

                window.location.href =
                    '/checkout/sucesso?pedido=' + data.pedidoId;

                return;
            }

            // Mercado Pago
            if (data.redirectUrl) {

                localStorage.removeItem('nemesis_cart_v1');

                window.location.href = data.redirectUrl;

                return;
            }

            // erro
            if (data.erro) {
                alert(data.erro);
            }

            if (btn) btn.disabled = false;
            if (btnText) btnText.textContent = 'Pagar agora';
        })
        .catch(function() {
            alert('Erro ao processar pagamento. Tente novamente.');
            if (btn) btn.disabled = false;
            if (btnText) btnText.textContent = 'Pagar agora';
        });
}